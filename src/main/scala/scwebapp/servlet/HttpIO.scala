package scwebapp.servlet

import java.util.concurrent.atomic.AtomicBoolean

import javax.servlet._
import javax.servlet.http._

import scutil.lang._

import scwebapp._

object HttpIO {
	def execute(servletRequest:HttpServletRequest, servletResponse:HttpServletResponse, handler:HttpHandler) {
		val request		= readRequest(servletRequest)
		val responder	= handler(request)
		responder match {
			case HttpResponderSync(response)	=>
				writeResponse(response, servletResponse)
			case HttpResponderAsync(responseCont, timeout, timeoutResponse, errorResponse)	=>
				typed[(HttpResponse=>Unit)=>Unit](responseCont)
			
				val asyncCtx	= servletRequest.startAsync()
				asyncCtx setTimeout timeout.millis
				
				val alive	= new AtomicBoolean(true)
				
				def completeWith(response:HttpResponse) {
					if (alive compareAndSet(true, false)) {
						writeResponse(response, servletResponse)
						asyncCtx.complete()
					}
				}
				asyncCtx addListener new AsyncListener {
					def onStartAsync(ev:AsyncEvent)	{}
					def onComplete(ev:AsyncEvent)	{ alive set false	}
					def onTimeout(ev:AsyncEvent)	{ completeWith(timeoutResponse())	}	
					def onError(ev:AsyncEvent)		{ completeWith(errorResponse())		}
				}
				responseCont(completeWith)
		}
	}
		
	private def readRequest(servletRequest:HttpServletRequest):HttpRequest	=
			new HttpRequestImpl(servletRequest)
		
	private def writeResponse(response:HttpResponse, servletResponse:HttpServletResponse) {
		response.reason match {
			case Some(reason)	=> servletResponse sendError (response.status.id, reason)
			case None			=> servletResponse setStatus response.status.id
		}
		
		response.headers foreach { case HeaderValue(k, v) =>
			servletResponse addHeader (k, v)
		}
		
		response.body intoOutputStream servletResponse.getOutputStream
	}
}
