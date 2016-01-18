package scwebapp.source

import java.util.zip.GZIPOutputStream

import scutil.lang._
import scutil.implicits._
import scutil.io.Charsets
import scutil.io.URIComponent

import scwebapp._
import scwebapp.method._
import scwebapp.status._
import scwebapp.format._
import scwebapp.factory.header._
import scwebapp.factory.mimeType._

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
		val eTag			= Quoting quoteSimple so"${URIComponent.utf_8 encode source.fileName}_${source.size.toString}_${source.lastModified.millis.toString}"
		val expires			= HttpDate.now + SourceHandler.defaultExpireTime

		val requestHeaders	= request.headers
		
		val ifNoneMatch		= requestHeaders firstString	"If-None-Match"
		val ifModifiedSince	= requestHeaders firstDate		"If-Modified-Since"
		val notModified	=
				(ifNoneMatch		exists (Matchers xmatch eTag))	||
				ifNoneMatch.isEmpty &&
				(ifModifiedSince	exists (Matchers lastModified lastModified))
		if (notModified) {
			return HttpResponse(
				NOT_MODIFIED,	None,
				Vector(
					ETag(eTag),
					Expires(expires)
				)
			)
		}

		val ifMatch				= requestHeaders firstString	"If-Match"
		val ifUnmodifiedSince	= requestHeaders firstDate		"If-Unmodified-Since"
		val preconditionFailed	=
				(ifMatch			exists !(Matchers xmatch eTag))	||
				ifMatch.isEmpty &&
				(ifUnmodifiedSince	exists !(Matchers lastModified lastModified))
		if (preconditionFailed) {
			return HttpResponse(PRECONDITION_FAILED)
		}

		// TODO should check format first
		val ifRange		= requestHeaders firstString	"If-Range"
		val ifRangeTime	= requestHeaders firstDate		"If-Range"
		val needsFull	=
				(ifRange		exists { _ != eTag	}) &&
				(ifRangeTime	exists { _ + HttpDuration.second < lastModified	})
				
		val range		= requestHeaders firstString "Range"
		val rangesRaw	= range map (HttpParser parseRangeHeader source.size)
		
		// TODO should we fail when needsFull but range headers are invalid?
		// TODO should we return full when there is a range header, but without any valid range?
		val full:RequestRange			= RequestRange full source.size
		val ranges:ISeq[RequestRange]	=
				(needsFull, rangesRaw) match {
					case (true,		_)										=> Vector(full)
					case (false,	None)									=> Vector(full)
					case (false,	Some(Some(ranges)))	if ranges.nonEmpty	=> ranges
					case _	=>
						val outRange	= ResponseRange total source.size
						return HttpResponse(
							REQUESTED_RANGE_NOT_SATISFIABLE,	None,
							Vector(
								ContentRange(outRange)
							)
						)
				}
		
		val acceptEncoding		= requestHeaders firstString "Accept-Encoding"
		val acceptsGzip:Boolean	=
				enableGZIP &&
				(acceptEncoding exists (Matchers acceptEncoding "gzip"))
		
		val accept	= requestHeaders firstString "Accept"
		val inline	=
				enableInline &&
				(accept exists (Matchers accept contentType))
			
		val disposition		=
				Disposition(
					inline cata (DispositionAttachment, DispositionInline),
					Some(source.fileName),
					Some(source.fileName)
				)

		val standardHeaders	=
				Vector(
					XContentTypeOptions("nosniff"),
					ContentDisposition(disposition),
					AcceptRanges("bytes"),
					ETag(eTag),
					LastModified(lastModified),
					Expires(expires)
				)
			
		// NOTE does not GZIP except for full range
		// servers and browsers seem to disagree on whether offsets should
		// apply to the body data before or after compression
		ranges match {
			case x if x forall { _ == full }	=>
				val r:RequestRange	= full
				HttpResponse(
					OK,	None,
					standardHeaders ++
					Vector(
						ContentType(contentType),
						ContentRange(r.toResponseRange)
					) ++ (
						if (acceptsGzip)		Vector(ContentEncoding(ContentEncodingGzip))
						else					Vector(ContentLength(r.length))
					),
						 if (!includeContent)	HttpOutput.empty
					else if (acceptsGzip)		rangeOutput(r.range) gzip SourceHandler.gzipBufferSize
					else						rangeOutput(r.range)
				)
			case ISeq(r)	=>
				HttpResponse(
					PARTIAL_CONTENT, None,
					standardHeaders ++
					Vector(
						ContentType(contentType),
						ContentRange(r.toResponseRange),
						ContentLength(r.length)
					),
					if (includeContent)	rangeOutput(r.range)
					else				HttpOutput.empty
				)
			case ranges	=>
				val boundary	= MultipartUtil.multipartBoundary()
				val ct			= multipart_byteranges_boundary(boundary)
				
				def boundaryOutput(r:ResponseRange):HttpOutput	=
						crlfOutput(
							"",
							so"--${boundary}",
							so"Content-Type: ${contentType.value}",
							so"Content-Range: ${ResponseRange unparse r}"
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
							.flatMap	{ r => Vector(boundaryOutput(r.toResponseRange), rangeOutput(r.range)) }
							.append		(finishOutput)
							.into		(HttpOutput.concat)
						}
						else HttpOutput.empty
						
				HttpResponse(
					PARTIAL_CONTENT, None,
					standardHeaders ++
					Vector(
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
	
	//------------------------------------------------------------------------------
	// TODO use HttpParser for these
	
	private object Matchers {
		// TODO eTag is quoted and may be prefixed with W/
		def xmatch(requirement:String):Predicate[String]	=
				header	=>
						splitList(header).toSet	containsAny
						Set(requirement, "*")
				
		def lastModified(requirement:HttpDate):Predicate[HttpDate]	=
				header =>
						header + HttpDuration.second > requirement
						
		def acceptEncoding(encoding:String):Predicate[String]	=
				header	=> {
					splitListParameterless(header).toSet containsAny
					Set(encoding, "*")
				}
				
		def accept(contentType:MimeType):Predicate[String]	=
				header => {
					// TODO should this match mime type parameters, too?
					splitListParameterless(header).toSet containsAny
					Set(
						so"${contentType.major}/${contentType.minor}",
						so"${contentType.major}/*",
						so"*/*"
					)
				}
						
		private def splitList(s:String):ISeq[String]	=
				s splitAroundChar ',' map { _.trim } filter { _.nonEmpty }
			
		private def splitListParameterless(s:String):ISeq[String]	=
				splitList(s) flatMap stripParam
			
		private def stripParam(s:String):Option[String]	=
				s replaceAll ("\\s*;.*", "") guardBy { _.nonEmpty }
	}
}
