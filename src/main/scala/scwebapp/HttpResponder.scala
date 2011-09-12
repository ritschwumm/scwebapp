package scwebapp

import java.io._
import java.nio.charset.Charset
import javax.servlet._
import javax.servlet.http._

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

	def AddCookie(cookie:Cookie):HttpResponder				= _ addCookie	cookie
	def AddHeader(name:String, value:String):HttpResponder	= _ addHeader	(name, value)
	// SendError
	// SendRedirect
	// SetStatus
	
	// response extension
	
	val NoCache:HttpResponder								= _.noCache
	def Unauthorized(realm:String):HttpResponder			= _ unauthorized		realm
	def Redirect(path:String):HttpResponder					= _ redirect			path
	def SetStatus(status:HttpStatusCode):HttpResponder		= _ setStatus			status
	def SetEncoding(encoding:Charset):HttpResponder			= _ setEncoding			encoding
	def SetContentType(contentType:MimeType):HttpResponder	= _ setContentType		contentType
	def SetContentLength(contentLength:Long):HttpResponder	= _ setContentLength	contentLength
	def SendString(string:String):HttpResponder				= _ sendString			string
	def SendFile(file:File):HttpResponder					= _ sendFile			file
	def SendStream(stream:InputStream):HttpResponder		= _ sendStream			stream
	def SendStringGZIP(string:String):HttpResponder			= _ sendStringGZIP		string
	def SendFileGZIP(file:File):HttpResponder				= _ sendFileGZIP		file
	def SendStreamGZIP(stream:InputStream):HttpResponder	= _ sendStreamGZIP		stream
	
	// extras
	
	// the string function gets passed a function to encode urls
	def SendStringWithUrls(string:(String=>String)=>String):HttpResponder	= 
			response => response sendString string(response.encodeURL)
}
