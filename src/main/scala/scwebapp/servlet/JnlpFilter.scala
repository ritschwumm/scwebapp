package scwebapp.servlet

import java.io._

import javax.servlet._
import javax.servlet.http._

import scala.collection.mutable

import scutil.log._

/**
replaces "$$codebase" with the parent of the JNLP file itself
to allow deploying the application to different servers without changing
the original jnlp file
*/
final class JnlpFilter extends Filter with Logging {
	@volatile private var filterConfig:Option[FilterConfig] 	= None
	
	def init(filterConfig:FilterConfig) {
		this.filterConfig	= Some(filterConfig)
	}
	
	def destroy() {
		this.filterConfig	= None
	}
	
	private def charset:String	=
			configCharset getOrElse "UTF-8"
		
	private def configCharset:Option[String]	=
			for {
				config	<- filterConfig
				charset	<- Option(config getInitParameter "charset")
			}
			yield charset
			
	//------------------------------------------------------------------------------
		
	def doFilter(request:ServletRequest, response:ServletResponse, filterChain:FilterChain) {
		val	httpRequest		= request.asInstanceOf[HttpServletRequest]
		val	httpResponse	= response.asInstanceOf[HttpServletResponse]
		
		val	wrapper	= new ResponseWrapper(httpResponse)
		filterChain doFilter (httpRequest, wrapper)
		if (wrapper.failed) {
			ERROR("wrapped failed")
		}
		else {
			val input		= new String(wrapper.written, charset)
			val codeBase	= httpRequest.getRequestURL.toString replaceAll ("/[^/]*$", "/")
			val patched		= input replace ("$$codebase", codeBase)
			val output		= patched getBytes charset
			
			httpResponse setContentLength	output.size
			httpResponse.getOutputStream	write	output
			httpResponse.getOutputStream 	flush	()
		}
	}
	
	/** swallows the response and makes it accessible via the get method  */
	private final class ResponseWrapper(response:HttpServletResponse) extends HttpServletResponseWrapper(response) with Logging {
		private val buffer			= new mutable.ArrayBuffer[Byte]
		private val outputStream	= new ServletOutputStream {
			def write(byt:Int) {
				buffer += byt.toByte
			}
			def isReady:Boolean								= false
			def setWriteListener(it:WriteListener): Unit	= ()
		}
		private val writer	= new PrintWriter(new OutputStreamWriter(outputStream, charset))
	
		// BETTER use HttpStatus	
		def failed:Boolean	=
				response.getStatus != 200	&&
				response.getStatus != 304
		
		/** provide the response string */
		def written:Array[Byte]	= buffer.toArray
		
		override def getWriter():PrintWriter				= writer
		override def getOutputStream():ServletOutputStream	= outputStream
	}
}
