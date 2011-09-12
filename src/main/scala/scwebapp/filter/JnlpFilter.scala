package scwebapp.filter

import java.io._
import javax.servlet._
import javax.servlet.http._

import scala.collection.mutable

import scutil.log.Logging

/** 
replaces "$$codebase" with the parent of the JNLP file itself
to allow deploying the application to different servers without changing
the original jnlp file
*/
final class JnlpFilter extends Filter with Logging {
	// TODO hardcoded
	private val charset	= "UTF-8"
	
	def init(filterConfig:FilterConfig) {}
	def destroy() {}
	
	def doFilter(request:ServletRequest, response:ServletResponse, filterChain:FilterChain) {
		val	httpRequest		= request.asInstanceOf[HttpServletRequest]
		val	httpResponse	= response.asInstanceOf[HttpServletResponse]
		
		val	wrapper	= new ResponseWrapper(httpResponse)
		filterChain doFilter (httpRequest, wrapper)
		if (wrapper.failed)	return
		
		val input		= new String(wrapper.written, charset)
		val codeBase	= httpRequest.getRequestURL.toString replaceAll ("/[^/]*$", "/")
		val patched		= input replace ("$$codeBase", codeBase)
		val output		= patched getBytes charset
		
		httpResponse setContentLength	output.size
		httpResponse.getOutputStream	write	output
		httpResponse.getOutputStream 	flush	()
	}
	
	/** swallows the response and makes it accessible via the get method  */
	private final class ResponseWrapper(response:HttpServletResponse) extends HttpServletResponseWrapper(response) with Logging {
		private val buffer			= new mutable.ArrayBuffer[Byte]
		private val outputStream	= new ServletOutputStream {
			def write(byt:Int) { 
				buffer += byt.toByte 
			}
		}
		private val writer	= new PrintWriter(new OutputStreamWriter(outputStream, charset))
		
		/** provide the response string */
		def written:Array[Byte]	= buffer.toArray
		
		override def getWriter():PrintWriter				= writer
		override def getOutputStream():ServletOutputStream	= outputStream
		
		// TODO use response.getStatus (in servlet 2.0)
		var failed	= false
		
		override def sendError(sc:Int) { 
			failed	= true
			super.sendError(sc)			
		}
		override def sendError(sc:Int, msg:String) {
			failed	= true
			super.sendError(sc, msg)	
		}
	}
}
