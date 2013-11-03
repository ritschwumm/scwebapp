package scwebapp

import java.io._
import java.nio.charset.Charset

import javax.servlet._
import javax.servlet.http._

import scutil.lang._
import scutil.Implicits._
import scutil.time._

import HttpImplicits._
import HttpMethodEnum._
import HttpStatusEnum._

object HttpInstances {
	//------------------------------------------------------------------------------
	//## handlers
	
	def Respond(responder:HttpResponder):HttpHandler	=
			constant(responder)
		
	//------------------------------------------------------------------------------
	//## partial handlers
	
	val Reject:HttpPHandler	= 
			constant(None)
	
	//------------------------------------------------------------------------------
	//## predicates
	
	def Method(method:HttpMethod):HttpPredicate	=
			_.getMethod.toUpperCase ==== method.id.toUpperCase
			
	def FullPath(path:String, encoding:Charset):HttpPredicate	=
			it => (it fullPath encoding) ==== path
			
	def FullPathRaw(path:String):HttpPredicate	=
			_.fullPathRaw ==== path
		
	def PathInfo(path:String, encoding:Charset):HttpPredicate	=
			_ pathInfo encoding exists { _ ==== path }
		
	def PathInfoRaw(path:String):HttpPredicate	=
			_.pathInfoRaw exists { _ ==== path }
			
	def ServletPath(path:String):HttpPredicate	=
			_.getServletPath ==== path
		
	//------------------------------------------------------------------------------
	//## responders
	
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
		val	age	= maxAge	cata (-1, _.millis / 1000 toInt)
		cookie	setMaxAge	age
		cookie	setSecure	secure
		cookie	setVersion	version	
		it addCookie cookie
	}
	
	def AddHeader(name:String, value:String):HttpResponder		= _ addHeader			(name, value)
	// SendError
	// SendRedirect
	// SetStatus
	
	// response extension
	
	val NoCache:HttpResponder									= _ noCache				()
	def Unauthorized(realm:String):HttpResponder				= _ unauthorized		realm
	def Redirect(path:String):HttpResponder						= _ redirect			path
	def SetStatus(status:HttpStatus):HttpResponder				= _ setStatus			status
	def SetEncoding(encoding:Charset):HttpResponder				= _ setEncoding			encoding
	def SetContentType(contentType:MimeType):HttpResponder		= _ setContentType		contentType
	def SetContentLength(contentLength:Long):HttpResponder		= _ setContentLength	contentLength
	
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
	def WithEncodeLink(mkResponder:(String=>String)=>HttpResponder):HttpResponder	=
			response	=> mkResponder(response.encodeURL)(response)
		
	/** mkString gets passed a function encoding a path suitable for a link */
	def SendStringWithLinks(mkString:(String=>String)=>String):HttpResponder	=
			WithEncodeLink(mkString andThen SendString)
		
	val Pass:HttpResponder	= constant(())
}
