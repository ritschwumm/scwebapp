package scwebapp.source

import java.util.zip.GZIPOutputStream

import scutil.lang._
import scutil.implicits._
import scutil.io.Charsets
import scutil.io.URIComponent

import scwebapp._
import scwebapp.method._
import scwebapp.status._
import scwebapp.data._
import scwebapp.format._
import scwebapp.header._
import scwebapp.factory.mimeType._
import scwebapp.factory.header._

object SourceHandler {
	private val defaultExpireTime	= HttpDuration.week
	private val gzipBufferSize		= 8192
}

// @see https://github.com/apache/tomcat/blob/trunk/java/org/apache/catalina/servlets/DefaultServlet.java
final class SourceHandler(source:Source, enableInline:Boolean, enableGZIP:Boolean) extends HttpHandler {
	def apply(request:HttpRequest):HttpResponder	=
			HttpResponder(
				request.method match {
					case Win(GET)	=> respond(request, true)
					case Win(HEAD)	=> respond(request, false)
					case _			=> HttpResponse(METHOD_NOT_ALLOWED)
				}
			)
			
	private def respond(request:HttpRequest, includeContent:Boolean):HttpResponse	= {
		var contentType		= source.mimeType
		val lastModified	= HttpDate fromMilliInstant source.lastModified
		// with URL-encoding we're safe with whitespace and line separators
		val eTag			= ETagValue(false, HttpUnparsers quotedString so"${URIComponent.utf_8 encode source.fileName}_${source.size.toString}_${source.lastModified.millis.toString}")
		val expires			= HttpDate.now + SourceHandler.defaultExpireTime

		val requestHeaders	= request.headers
		
		val ifNoneMatch		= (requestHeaders first IfNoneMatch).toOption.flatten
		val ifModifiedSince	= (requestHeaders first IfModifiedSince).toOption.flatten
		val notModified		=
				ifNoneMatch cata (
					(ifModifiedSince	exists { it => (it wasModified lastModified) }),
					it => (it matches eTag)
				)
		if (notModified) {
			return HttpResponse(
				NOT_MODIFIED,	None,
				HeaderValues(
					ETag(eTag),
					Expires(expires)
				)
			)
		}

		val ifMatch				= (requestHeaders first IfMatch).toOption.flatten
		val ifUnmodifiedSince	= (requestHeaders first IfUnmodifiedSince).toOption.flatten
		val preconditionFailed	=
				ifMatch cata (
					(ifUnmodifiedSince	exists { it => !(it wasModified lastModified) }),
					it => !(it matches eTag)
				)
		if (preconditionFailed) {
			return HttpResponse(PRECONDITION_FAILED)
		}

		val needsFull	=
				requestHeaders first IfRange match {
					case Fail(_)		=> true
					case Win(None)		=> false
					case Win(Some(x))	=> x needsFull (eTag, lastModified)
				}
			
		val total		= source.size
		val range		= requestHeaders first Range
		val rangesRaw	= range map { _ map { _ inclusiveRanges total } }
		
		// TODO should we fail when needsFull but range headers are invalid?
		val full:InclusiveRange			= InclusiveRange full total
		val ranges:ISeq[InclusiveRange]	=
				(needsFull, rangesRaw) match {
					case (true,		_)										=> Vector(full)
					case (false,	Win(None))								=> Vector(full)
					case (false,	Win(Some(ranges)))	if ranges.nonEmpty	=> ranges
					case _	=>
						val outRange	= ContentRangeValue total source.size
						return HttpResponse(
							REQUESTED_RANGE_NOT_SATISFIABLE,	None,
							Vector(
								ContentRange(outRange)
							)
						)
				}
		
		val acceptEncoding		= (requestHeaders first AcceptEncoding).toOption.flatten
		val acceptsGzip:Boolean	=
				enableGZIP &&
				(acceptEncoding exists { _ accepts AcceptEncodingOther(ContentEncodingGzip) })
		
		val accept	= (requestHeaders first Accept).toOption.flatten
		val inline	=
				enableInline &&
				(accept exists { _ accepts contentType })
			
		val contentDisposition		=
				ContentDisposition(
					inline cata (ContentDispositionAttachment, ContentDispositionInline),
					Some(source.fileName)
				)

		val standardHeaders	=
				HeaderValues(
					XContentTypeOptions("nosniff"),
					contentDisposition,
					AcceptRanges(RangeTypeBytes),
					ETag(eTag),
					LastModified(lastModified),
					Expires(expires)
				)
			
		// NOTE does not GZIP except for full range
		// servers and browsers seem to disagree on whether offsets should
		// apply to the body data before or after compression
		ranges match {
			case x if x forall { _ == full }	=>
				val r:InclusiveRange	= full
				HttpResponse(
					OK,	None,
					standardHeaders ++
					HeaderValues(
						ContentType(contentType),
						ContentRange(ContentRangeValue full (r, total)),
						if (acceptsGzip)		ContentEncoding(ContentEncodingGzip)
						else					ContentLength(r.length)
					),
						 if (!includeContent)	HttpOutput.empty
					else if (acceptsGzip)		rangeOutput(r) gzip SourceHandler.gzipBufferSize
					else						rangeOutput(r)
				)
			case ISeq(r)	=>
				HttpResponse(
					PARTIAL_CONTENT, None,
					standardHeaders ++
					HeaderValues(
						ContentType(contentType),
						ContentRange(ContentRangeValue full (r, total)),
						ContentLength(r.length)
					),
					if (includeContent)	rangeOutput(r)
					else				HttpOutput.empty
				)
			case ranges	=>
				val boundary	= MultipartUtil.multipartBoundary()
				val ct			= multipart_byteranges_boundary(boundary)
				
				def boundaryOutput(r:ContentRangeValue):HttpOutput	=
						crlfOutput(
							"",
							so"--${boundary}",
							ContentType unparse ContentType(contentType),
							ContentRange unparse ContentRange(r)
						)
				
				def finishOutput:HttpOutput	=
						crlfOutput(
							"",
							so"--${boundary}--"
						)
						
				def crlfOutput(ss:String*):HttpOutput	=
						HttpOutput.writeString(
							Charsets.us_ascii,
							ss map (_ + "\r\n") mkString ""
						)
								
				val body	=
						if (includeContent) {
							ranges
							.flatMap	{ r => Vector(boundaryOutput(ContentRangeValue full (r, total)), rangeOutput(r)) }
							.append		(finishOutput)
							.into		(HttpOutput.concat)
						}
						else HttpOutput.empty
						
				HttpResponse(
					PARTIAL_CONTENT, None,
					standardHeaders ++
					HeaderValues(
						ContentType(ct)
					),
					body
				)
		}
	}
	
	//------------------------------------------------------------------------------
	
	private def rangeOutput(range:InclusiveRange):HttpOutput	=
			HttpOutput withOutputStream {
				source range (range.start, range.length) transferTo _
			}
}
