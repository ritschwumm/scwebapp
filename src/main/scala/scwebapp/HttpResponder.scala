package scwebapp

import java.io._
import java.nio.charset.Charset
import javax.servlet._
import javax.servlet.http._

import scutil.Functions._
import scutil.Implicits._
import scutil.Resource._

import HttpServletResponseImplicits._
import HttpStatusCodes._

object HttpResponder {
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

	def AddCookie(cookie:Cookie):HttpResponder					= _ addCookie	cookie
	def AddHeader(name:String, value:String):HttpResponder		= _ addHeader	(name, value)
	// SendError
	// SendRedirect
	// SetStatus
	
	// response extension
	
	val NoCache:HttpResponder									= _ noCache				()
	def Unauthorized(realm:String):HttpResponder				= _ unauthorized		realm
	def Redirect(path:String):HttpResponder						= _ redirect			path
	def SetStatus(status:HttpStatusCode):HttpResponder			= _ setStatus			status
	def SetEncoding(encoding:Charset):HttpResponder				= _ setEncoding			encoding
	def SetContentType(contentType:MimeType):HttpResponder		= _ setContentType		contentType
	def SetContentLength(contentLength:Long):HttpResponder		= _ setContentLength	contentLength
	
	def StreamFrom(in:Thunk[InputStream]):HttpResponder			= _ streamFrom in
	def WriteFrom(in:Thunk[Reader]):HttpResponder				= _ writeFrom in
	
	def SendString(string:String):HttpResponder					= _ sendString			string
	def SendFile(file:File):HttpResponder						= _ sendFile			file
	
	def StreamFromGZIP(stream:Thunk[InputStream]):HttpResponder	= _ streamFromGZIP		stream
	def WriteFromGZIP(reader:Thunk[Reader]):HttpResponder		= _ writeFromGZIP		reader
	
	def SendStringGZIP(string:String):HttpResponder				= _ sendStringGZIP		string
	def SendFileGZIP(file:File):HttpResponder					= _ sendFileGZIP		file
	
	
	// extras
	
	/** the string function gets passed a function to encode urls */
	def WithEncodeLink(mkResponder:(String=>String)=>HttpResponder):HttpResponder	=
			response	=> mkResponder(response.encodeURL)
		
	/** mkString gets passed a function encoding a path suitable for a link */
	def SendStringWithLinks(mkString:(String=>String)=>String):HttpResponder	=
			WithEncodeLink(mkString andThen SendString)
}
