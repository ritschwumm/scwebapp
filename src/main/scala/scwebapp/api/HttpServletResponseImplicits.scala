package scwebapp
package api

import java.io._
import java.nio.charset.Charset
import java.util.zip.GZIPOutputStream
import javax.servlet.http._

import scutil.lang._
import scutil.Implicits._
import scutil.Resource._

import HttpStatusEnum._

object HttpServletResponseImplicits extends HttpServletResponseImplicits
	
trait HttpServletResponseImplicits {
	implicit def extendHttpServletResponse(delegate:HttpServletResponse):HttpServletResponseExtension	= 
			new HttpServletResponseExtension(delegate)
}

final class HttpServletResponseExtension(delegate:HttpServletResponse) {
	def noCache() {
		delegate addHeader ("Cache-Control",	"no-cache, must-revalidate")
		delegate addHeader ("Expires",			"1 Jan 1971")
	}
	
	/** see http://www.ietf.org/rfc/rfc2617.txt */
	def unauthorized(realm:String) {
		delegate setHeader ("WWW-Authenticate", "Basic realm=\"" + realm + "\"")
		setStatus(UNAUTHORIZED)
	}

	/** redirect the client to another URL */
	def redirect(path:String) {
		delegate sendRedirect (delegate encodeRedirectURL path)
	}
	
	//------------------------------------------------------------------------------
		
	def setEncoding(encoding:Charset) {
		delegate setCharacterEncoding encoding.name
	}

	def setContentType(contentType:MimeType) {
		delegate setContentType contentType.value
	}
	
	/*
	// BETTER implement ContentEncoding (?)
	def setContentEncoding(contentEncoding:ContentEncoding) {
		delegate setHeader  ("Content-Encoding", contentEncoding.value)	
	}
	*/
	
	def setContentLength(contentLength:Long) {
		delegate setHeader ("Content-Length", contentLength.toString)
	}
	
	def setStatus(status:HttpStatus) {
		delegate setStatus status.id
	}
	
	//------------------------------------------------------------------------------
	
	def sendString(string:String) {
		delegate.getWriter write string
	}
	
	def sendFile(file:File) {
		// BETTER include content length here?
		streamFrom(thunk(new FileInputStream(file)))
	}
	
	def streamFrom(stream:Thunk[InputStream]) {
		// TODO handle exceptions
		stream() use { _ copyTo delegate.getOutputStream }
		delegate.getOutputStream.flush()
	}
	
	def writeFrom(reader:Thunk[Reader]) {
		// TODO handle exceptions
		reader() use { _ copyTo delegate.getWriter }
		delegate.getOutputStream.flush()
	}
	
	//------------------------------------------------------------------------------
	
	def sendStringGZIP(string:String) {
		writerGZIP { _ write string }
	}
	
	def sendFileGZIP(file:File) {
		streamFromGZIP(thunk(new FileInputStream(file)))
	}
	
	def streamFromGZIP(stream:Thunk[InputStream]) {
		outputStreamGZIP { out =>
			stream() use { in =>
				in copyTo out
			}
		}
	}
	
	def writeFromGZIP(reader:Thunk[Reader]) {
		writerGZIP { out =>
			reader() use { in =>
				in copyTo out
			}
		}
	}
	
	// TODO hardcoded
	private val gzipBufferSize	= 8192
	
	// TODO handle exceptions
	private def writerGZIP(func:Effect[Writer]) {
		val encoding	= delegate.getCharacterEncoding nullError "missing response character encoding"
		outputStreamGZIP { stream =>
			val writer	= new OutputStreamWriter(stream, encoding)
			func(writer)
			writer.flush()
		}
	}
	
	// TODO handle exceptions
	private def outputStreamGZIP(func:Effect[OutputStream]) {
		val stream	= new GZIPOutputStream(delegate.getOutputStream, gzipBufferSize)
		func(stream)
		stream.finish()
	}
	
	//------------------------------------------------------------------------------
	
	// /** never gzip-compress texts with less than this characters */
	// private val MIN_GZIP_LENGTH	= 2048
	
	// /** write a string to the response writer, if possible (and sensible) with gzip-encoding */
	// protected def writeGzip(text:String) { // throws IOException
	// 	val encodings	= request getHeader "Accept-Encoding"
	// 	val useGzip		= 
	// 			encodings != null 			&&
	// 			(encodings contains "gzip")	&&
	// 			text.length >= MIN_GZIP_LENGTH
	// 	if (useGzip) {
	// 		response setHeader	("Content-Encoding",	"gzip")
	// 		response setHeader	("Vary", 				"Accept-Encoding")
			
	// 		val encoding	= response.getCharacterEncoding
	// 		if (encoding == null)	sys error "response.CharacterEncoding must not be null"
			
	// 		val stream	= new GZIPOutputStream(response.getOutputStream)
	// 		val writer	= new OutputStreamWriter(stream, encoding)
	// 		writer write text
	// 		writer.flush()
	// 		stream.finish()
	// 	}
	// 	else {
	// 		write(text)
	// 	}
	// }
}
