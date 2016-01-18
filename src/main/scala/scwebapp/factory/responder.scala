package scwebapp
package factory

import java.io._
import java.nio.charset.Charset

import javax.servlet.http._

import scutil.lang._
import scutil.implicits._
import scutil.time._

import scwebapp.implicits._

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
		path:Option[String]				= None,	
		domain:Option[String]			= None,
		comment:Option[String]			= None,
		maxAge:Option[MilliDuration]	= None,	// None deletes on browser exit, zero deletes immediately
		secure:Boolean					= false,
		version:Int						= 0		// 0=netscape, 1=RFC
	):HttpResponder = {
		val cookie	= new Cookie(name, value)
		path	foreach cookie.setPath
		domain	foreach cookie.setDomain
		comment	foreach	cookie.setComment
		val	age	= maxAge cata (-1, it => ((it.millis + 1000 - 1) / 1000).toInt)
		cookie	setMaxAge	age
		cookie	setSecure	secure
		cookie	setVersion	version	
		_ addCookie cookie
	}
	
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
	
	def StreamFrom(in:Thunk[InputStream]):HttpResponder					= Body(HttpOutput pipeInputStream in)
	def StreamFromGZIP(in:Thunk[InputStream]):HttpResponder				= Body(HttpOutput pipeInputStream in gzip gzipBufferSize)
	def SendFile(file:File):HttpResponder								= Body(HttpOutput writeFile file)
	def SendFileGZIP(file:File):HttpResponder							= Body(HttpOutput writeFile file gzip gzipBufferSize)
	
	def WriteFrom(encoding:Charset, in:Thunk[Reader]):HttpResponder		= Body((HttpOutput pipeReader encoding)(in))
	def WriteFromGZIP(encoding:Charset, in:Thunk[Reader]):HttpResponder	= Body((HttpOutput pipeReader encoding)(in) gzip gzipBufferSize)
	def SendString(encoding:Charset, string:String):HttpResponder		= Body((HttpOutput writeString encoding)(string))
	def SendStringGZIP(encoding:Charset, string:String):HttpResponder	= Body((HttpOutput writeString encoding)(string) gzip gzipBufferSize)
	
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
