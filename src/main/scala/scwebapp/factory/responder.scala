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
	// base servlet
	
	// FlushBuffer
	// Reset
	// ResetBuffer
	// SetBufferSize
	// SetCharacterEncoding
	// SetContentLength
	// SetContentType
	// SetLocale
	
	// http servlet
	
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
	):HttpResponder = { it	=>
		val cookie	= new Cookie(name, value)
		path	foreach cookie.setPath
		domain	foreach cookie.setDomain
		comment	foreach	cookie.setComment
		val	age	= maxAge cata (-1, it => (it.millis / 1000).toInt)
		cookie	setMaxAge	age
		cookie	setSecure	secure
		cookie	setVersion	version	
		it addCookie cookie
	}
	
	def AddHeader(name:String, value:String):HttpResponder		= _ addHeader			(name, value)
	// SendError
	
	// response extension
	
	val NoCache:HttpResponder									= _ noCache				()
	def Unauthorized(realm:String):HttpResponder				= _ unauthorized		realm
	def Redirect(path:String):HttpResponder						= _ redirect			path
	def SetStatus(status:HttpStatus):HttpResponder				= _ setStatus			status
	def SetEncoding(encoding:Charset):HttpResponder				= _ setEncoding			encoding
	def SetContentType(contentType:MimeType):HttpResponder		= _ setContentType		contentType
	def SetContentLength(contentLength:Long):HttpResponder		= _ setContentLength	contentLength
	
	def Send(data:HttpOutput):HttpResponder						= _ send data
	
	def StreamFrom(in:Thunk[InputStream]):HttpResponder			= _ streamFrom			in
	def WriteFrom(in:Thunk[Reader]):HttpResponder				= _ writeFrom			in
	
	def SendString(string:String):HttpResponder					= _ sendString			string
	def SendFile(file:File):HttpResponder						= _ sendFile			file
	
	def StreamFromGZIP(stream:Thunk[InputStream]):HttpResponder	= _ streamFromGZIP		stream
	def WriteFromGZIP(reader:Thunk[Reader]):HttpResponder		= _ writeFromGZIP		reader
	
	def SendStringGZIP(string:String):HttpResponder				= _ sendStringGZIP		string
	def SendFileGZIP(file:File):HttpResponder					= _ sendFileGZIP		file
	
	// extras
	
	/** the string function gets passed a function to encode urls */
	def WithEncodeLink(mkResponder:Endo[String]=>HttpResponder):HttpResponder	=
			response	=> mkResponder(response.encodeURL)(response)
		
	/** mkString gets passed a function encoding a path suitable for a link */
	def SendStringWithLinks(mkString:Endo[String]=>String):HttpResponder	=
			WithEncodeLink(mkString andThen SendString)
		
	val Pass:HttpResponder	= constant(())
}
