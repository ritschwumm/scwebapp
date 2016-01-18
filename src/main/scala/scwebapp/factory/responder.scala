package scwebapp
package factory

import java.io._
import java.nio.charset.Charset

import scutil.lang._

import scwebapp.implicits._
import scwebapp.format._

object responder extends responder

trait responder {
	val gzipBufferSize	= 8192
	
	//## primtive
	
	val Pass:HttpResponder	= constant(())
	
	def concat(responders:ISeq[HttpResponder]):HttpResponder	=
			(responders foldLeft Pass)(_ ~> _)
	
	//## base servlet
	
	// FlushBuffer
	// Reset
	// ResetBuffer
	// SetBufferSize
	// SetCharacterEncoding
	// SetContentLength
	// SetContentType
	// SetLocale
	
	//## http servlet
	
	// forbidden in name and value: [ ] ( ) = , " / ? @ : ;
	def AddCookie(
		name:String,
		value:String,
		domain:Option[String]		= None,
		path:Option[String]			= None,	
		comment:Option[String]		= None,
		maxAge:Option[HttpDuration]	= None,	// None deletes on browser exit, zero deletes immediately
		secure:Boolean				= false,
		httpOnly:Boolean			= false
		// version:Int				= 0		// 0=netscape, 1=RFC
	):HttpResponder =
			AddHeader(
				"Set-Cookie",
				HeaderUnparser setCookieValue (
					name, value, domain, path, comment, maxAge, secure, httpOnly
				)
			)
	
	def AddHeader(name:String, value:String):HttpResponder			= _ addHeader			(name, value)
	
	//## response extension
	
	val NoCache:HttpResponder										= _ noCache				()
	def Unauthorized(realm:String):HttpResponder					= _ unauthorized		realm
	def Redirect(path:String):HttpResponder							= _ redirect			path
	def SendError(status:HttpStatus, reason:String):HttpResponder	= _ sendError			(status, reason)
	def SetStatus(status:HttpStatus):HttpResponder					= _ setStatus			status
	def SetEncoding(encoding:Charset):HttpResponder					= _ setEncoding			encoding
	def SetContentType(contentType:MimeType):HttpResponder			= _ setContentType		contentType
	def SetContentLength(contentLength:Long):HttpResponder			= _ setContentLength	contentLength
	
	def Body(data:HttpOutput):HttpResponder							= _ body data
	
	def StreamFrom(in:Thunk[InputStream]):HttpResponder					= Body(HttpOutput pipeInputStream	in)
	def StreamFromGZIP(in:Thunk[InputStream]):HttpResponder				= Body(HttpOutput pipeInputStream	in		gzip gzipBufferSize)
	def SendFile(file:File):HttpResponder								= Body(HttpOutput writeFile			file)
	def SendFileGZIP(file:File):HttpResponder							= Body(HttpOutput writeFile			file	gzip gzipBufferSize)
	
	def WriteFrom(encoding:Charset, in:Thunk[Reader]):HttpResponder		= Body(HttpOutput pipeReader	(encoding, in))
	def WriteFromGZIP(encoding:Charset, in:Thunk[Reader]):HttpResponder	= Body(HttpOutput pipeReader	(encoding, in)		gzip gzipBufferSize)
	def SendString(encoding:Charset, string:String):HttpResponder		= Body(HttpOutput writeString	(encoding, string))
	def SendStringGZIP(encoding:Charset, string:String):HttpResponder	= Body(HttpOutput writeString	(encoding, string)	gzip gzipBufferSize)
	
	/*
	//## extras
	
	// mkString gets passed a function encoding a path suitable for a link
	def SendStringWithLinks(mkString:Endo[String]=>String):HttpResponder	=
			WithEncodeLink(mkString andThen SendString)
		
	// the string function gets passed a function to encode urls
	def WithEncodeLink(mkResponder:Endo[String]=>HttpResponder):HttpResponder	=
			response	=> mkResponder(response.encodeURL)(response)
	*/
}
