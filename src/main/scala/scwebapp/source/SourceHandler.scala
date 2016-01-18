package scwebapp.source

import java.io._
import java.util.zip.GZIPOutputStream

import javax.servlet.http._

import scutil.lang._
import scutil.implicits._
import scutil.io.URIComponent
import scutil.io.Charsets

import scwebapp._
import scwebapp.implicits._
import scwebapp.method._
import scwebapp.status._
import scwebapp.instances._

object SourceHandler {
	private val defaultBufferSize = 16384
	private val defaultExpireTime = 7L * 24 * 60 * 60	// seconds of a week
}

// @see https://github.com/apache/tomcat/blob/trunk/java/org/apache/catalina/servlets/DefaultServlet.java
final class SourceHandler(source:Source, enableInline:Boolean, enableGZIP:Boolean) extends HttpHandler {
	def apply(request:HttpServletRequest):HttpResponder	=
			request.method match {
				case GET	=> respond(request, true)
				case HEAD	=> respond(request, false)
				case _		=> SetStatus(METHOD_NOT_ALLOWED)
			}
			
	private def respond(request:HttpServletRequest, includeContent:Boolean):HttpResponder	= {
		var contentType		= source.mimeType
		val lastModified	= HttpDate fromMilliInstant source.lastModified
		// with URL-encoding we're safe with whitespace and line separators
		val eTag			= HeaderUnparsers quoteSimple so"${URIComponent encode source.fileName}_${source.size.toString}_${source.lastModified.millis.toString}"
		val expires			= HttpDate.now + SourceHandler.defaultExpireTime

		val requestHeaders	= request.headers
		
		val ifNoneMatch		= requestHeaders firstString	"If-None-Match"
		val ifModifiedSince	= requestHeaders firstDate		"If-Modified-Since"
		val notModified	=
				(ifNoneMatch		exists (Matchers xmatch eTag))	||
				ifNoneMatch.isEmpty &&
				(ifModifiedSince	exists (Matchers lastModified lastModified))
		if (notModified) {
			return	SetStatus(NOT_MODIFIED) ~>
					AddHeader("ETag",		eTag) ~>
					AddHeader("Expires",	HttpDateFormat unparse expires)
		}

		val ifMatch				= requestHeaders firstString	"If-Match"
		val ifUnmodifiedSince	= requestHeaders firstDate		"If-Unmodified-Since"
		val preconditionFailed	=
				(ifMatch			exists !(Matchers xmatch eTag))	||
				ifMatch.isEmpty &&
				(ifUnmodifiedSince	exists !(Matchers lastModified lastModified))
		if (preconditionFailed) {
			return SetStatus(PRECONDITION_FAILED)
		}

		// TODO should check format first
		val ifRange		= requestHeaders firstString	"If-Range"
		val ifRangeTime	= requestHeaders firstDate		"If-Range"
		val needsFull	=
				(ifRange		exists { _ != eTag				}) &&
				(ifRangeTime	exists { _ + 1 < lastModified	})
				
		val range		= requestHeaders firstString "Range"
		val rangesRaw	= range map (HeaderParsers parseRangeHeader source.size)
		
		// TODO should we fail when needsFull but range headers are invalid?
		val full:HttpRange	= HttpRange full source.size
		val ranges:ISeq[HttpRange]	=
				(needsFull, rangesRaw) match {
					case (true,		_)					=> Vector(full)
					case (false,	None)				=> Vector(full)
					case (false,	Some(Some(ranges)))	=> ranges
					case _	=>
							return	SetStatus(REQUESTED_RANGE_NOT_SATISFIABLE)	~>
									AddHeader("Content-Range", so"bytes */${source.size.toString}")
				}
		
		val acceptEncoding		= requestHeaders firstString "Accept-Encoding"
		val acceptsGzip:Boolean	=
				enableGZIP &&
				(acceptEncoding exists (Matchers acceptEncoding "gzip"))
		
		val accept	= requestHeaders firstString "Accept"
		val inline	=
				enableInline &&
				(accept exists (Matchers accept contentType))
			
		val disposition		= {
			val dispositionType	= inline cata ("attachment", "inline")
			val fileName		= HeaderUnparsers quoteSimple	source.fileName
			val fileNameStar	= HeaderUnparsers quoteStar		source.fileName
			 so"${dispositionType};filename=${fileName};filename*=${fileNameStar}"
		}

		def contentOrPass(contentResponder: =>HttpResponder):HttpResponder	=
				if (includeContent)	contentResponder
				else				Pass
				
		// NOTE does not GZIP except for full range
		// servers and browsers seem to disagree on whether offsets should
		// apply to the body data before or after compression
		val rangeResponder	=
				ranges match {
					case x if x forall { _ == full }	=>
						val r:HttpRange	= full
						SetContentType(contentType)									~>
						AddHeader("Content-Range", HeaderUnparsers formatRange r)	~>
						contentOrPass {
							if (acceptsGzip) {
								AddHeader("Content-Encoding", "gzip")	~>
								streamResponderGZIP(rangeTransfer(r))
							}
							else {
								SetContentLength(r.length)	~>
								streamResponder(rangeTransfer(r))
							}
						}
					case ISeq(r)	=>
						SetContentType(contentType)									~>
						AddHeader("Content-Range", HeaderUnparsers formatRange r)	~>
						SetContentLength(r.length)									~>
						SetStatus(PARTIAL_CONTENT)									~>
						contentOrPass {
							streamResponder(rangeTransfer(r))
						}
					case ranges	=>
						val boundary	= HttpUtil.multipartBoundary()
						val ct			= MimeType("multipart", "byteranges") addParameter ("boundary", boundary)
						SetContentType(ct)			~>
						SetStatus(PARTIAL_CONTENT)	~>
						contentOrPass {
							def crlfResponder(ss:String*):HttpResponder	=
									SendString(Charsets.us_ascii, ss map (_ + "\r\n") mkString "")
							
							def boundaryResponder(r:HttpRange):HttpResponder	=
									crlfResponder(
										"",
										so"--${boundary}",
										so"Content-Type: ${contentType.value}",
										so"Content-Range: ${HeaderUnparsers formatRange r}"
									)
							
							def finishResponder:HttpResponder	=
									crlfResponder(
										"",
										so"--${boundary}--"
									)
									
							def contentResponder(r:HttpRange):HttpResponder	=
									streamResponder(rangeTransfer(r))
								
							ranges
							.flatMap	{ r => Vector(boundaryResponder(r), contentResponder(r)) }
							.append		(finishResponder)
							.into		(concat)
						}
				}
				
		// TODO ugly hack
		HttpResponder { _ setBufferSize SourceHandler.defaultBufferSize }			~>
		AddHeader("X-Content-Type-Options",	"nosniff")								~>
		AddHeader("Content-Disposition",	disposition)							~>
		AddHeader("Accept-Ranges",			"bytes")								~>
		AddHeader("ETag",					eTag)									~>
		AddHeader("Last-Modified",			HttpDateFormat unparse lastModified)	~>
		AddHeader("Expires",				HttpDateFormat unparse expires)			~>
		rangeResponder
	}
	
	//------------------------------------------------------------------------------
	
	private def streamResponder(effect:Effect[OutputStream]):HttpResponder	=
			Body(HttpOutput withOutputStream effect)
		
	private def streamResponderGZIP(effect:Effect[OutputStream]):HttpResponder	=
			Body(HttpOutput withOutputStream effect gzip gzipBufferSize)
			
	private def rangeTransfer(range:HttpRange):Effect[OutputStream]	=
			source range (range.start, range.length) transferTo _
	
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
						header + 1 > requirement
						
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
