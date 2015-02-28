package scwebapp
package pimp

import java.io._
import java.nio.charset.Charset
import java.util.zip.GZIPOutputStream

import javax.servlet.http._

import scutil.lang._
import scutil.implicits._

import scwebapp.HttpOutput
import scwebapp.status._

object HttpServletResponseImplicits extends HttpServletResponseImplicits
	
trait HttpServletResponseImplicits {
	implicit def extendHttpServletResponse(peer:HttpServletResponse):HttpServletResponseExtension	=
			new HttpServletResponseExtension(peer)
}

final class HttpServletResponseExtension(peer:HttpServletResponse) {
	def noCache() {
		peer addHeader ("Cache-Control",	"no-cache, no-store, must-revalidate")
		peer addHeader ("Pragma",			"no-cache")
		peer addHeader ("Expires",			"0")
	}
	
	/** @see http://www.ietf.org/rfc/rfc2617.txt */
	def unauthorized(realm:String) {
		peer setHeader ("WWW-Authenticate", "Basic realm=\"" + realm + "\"")
		setStatus(UNAUTHORIZED)
	}

	/** redirect the client to another URL */
	def redirect(path:String) {
		peer sendRedirect (peer encodeRedirectURL path)
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
	
	/*
	// BETTER implement ContentEncoding (?)
	def setContentEncoding(contentEncoding:ContentEncoding) {
		peer setHeader  ("Content-Encoding", contentEncoding.value)	
	}
	*/
	
	def setContentLength(contentLength:Long) {
		peer setHeader ("Content-Length", contentLength.toString)
	}
	
	def setStatus(status:HttpStatus) {
		peer setStatus status.id
	}
	
	def addLongHeader(name:String, value:Long) {
		peer addHeader (name, value.toString)
	}
	
	def addHttpDateHeader(name:String, value:HttpDate) {
		peer addHeader (name, HttpDateFormat unparse value)
	}
	
	//------------------------------------------------------------------------------
	//## content
	
	def send(output:HttpOutput) {
		output transferTo peer.getOutputStream
	}
	
	// TODO getWriter is too much magic
	
	def sendString(string:String) {
		peer.getWriter write string
	}
	
	def sendFile(file:File) {
		// BETTER include content length here?
		streamFrom(thunk(new FileInputStream(file)))
	}
	
	def streamFrom(stream:Thunk[InputStream]) {
		// TODO handle exceptions
		stream() use { _ transferTo peer.getOutputStream }
		peer.getOutputStream.flush()
	}
	
	def writeFrom(reader:Thunk[Reader]) {
		// TODO handle exceptions
		reader() use { _ transferTo peer.getWriter }
		peer.getOutputStream.flush()
	}
	
	//------------------------------------------------------------------------------
	//## gzip content
	
	def sendStringGZIP(string:String) {
		writerGZIP { _ write string }
	}
	
	def sendFileGZIP(file:File) {
		streamFromGZIP(thunk(new FileInputStream(file)))
	}
	
	def streamFromGZIP(stream:Thunk[InputStream]) {
		outputStreamGZIP { out =>
			stream() use { in =>
				in transferTo out
			}
		}
	}
	
	def writeFromGZIP(reader:Thunk[Reader]) {
		writerGZIP { out =>
			reader() use { in =>
				in transferTo out
			}
		}
	}
	
	// TODO hardcoded
	private val gzipBufferSize	= 8192
	
	// TODO handle exceptions
	private def writerGZIP(func:Effect[Writer]) {
		// TODO morally wrong
		val encoding	= peer.getCharacterEncoding nullError "missing response character encoding"
		outputStreamGZIP { stream =>
			val writer	= new OutputStreamWriter(stream, encoding)
			func(writer)
			writer.flush()
		}
	}
	
	// TODO handle exceptions
	private def outputStreamGZIP(func:Effect[OutputStream]) {
		val stream	= new GZIPOutputStream(peer.getOutputStream, gzipBufferSize)
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
