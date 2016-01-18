package scwebapp
package pimp

import java.nio.charset.Charset
import java.util.zip.GZIPOutputStream

import javax.servlet.http._

import scutil.implicits._

import scwebapp.HttpOutput
import scwebapp.status._

object HttpServletResponseImplicits extends HttpServletResponseImplicits
	
trait HttpServletResponseImplicits {
	implicit def extendHttpServletResponse(peer:HttpServletResponse):HttpServletResponseExtension	=
			new HttpServletResponseExtension(peer)
}

final class HttpServletResponseExtension(peer:HttpServletResponse) {
	//------------------------------------------------------------------------------
	//## status
	
	/** @see http://www.ietf.org/rfc/rfc2617.txt */
	def unauthorized(realm:String) {
		peer setHeader ("WWW-Authenticate", so"""Basic realm="${realm}"""")
		setStatus(UNAUTHORIZED)
	}

	/** redirect the client to another URL */
	def redirect(path:String) {
		peer sendRedirect (peer encodeRedirectURL path)
	}
	
	def setStatus(status:HttpStatus) {
		peer setStatus status.id
	}
	
	def sendError(status:HttpStatus, reason:String) {
		peer sendError (status.id, reason)
	}
	
	//------------------------------------------------------------------------------
	//## headers
	
	/** this is a bit of magic: if used after setContentType it can append a charset parameter */
	def setEncoding(encoding:Charset) {
		peer setCharacterEncoding encoding.name
	}

	def setContentType(contentType:MimeType) {
		peer setContentType contentType.value
	}
	
	def setContentLength(contentLength:Long) {
		peer setHeader ("Content-Length", contentLength.toString)
	}
	
	/*
	// BETTER implement ContentEncoding (?)
	def setContentEncoding(contentEncoding:ContentEncoding) {
		peer setHeader  ("Content-Encoding", contentEncoding.value)	
	}
	*/
	
	def addLongHeader(name:String, value:Long) {
		peer addHeader (name, value.toString)
	}
	
	def addHttpDateHeader(name:String, value:HttpDate) {
		peer addHeader (name, HttpDate unparse value)
	}
	
	def noCache() {
		peer addHeader ("Cache-Control",	"no-cache, no-store, must-revalidate")
		peer addHeader ("Pragma",			"no-cache")
		peer addHeader ("Expires",			"0")
	}
	
	//------------------------------------------------------------------------------
	//## body
	
	def body(output:HttpOutput) {
		output intoOutputStream peer.getOutputStream
	}
}
