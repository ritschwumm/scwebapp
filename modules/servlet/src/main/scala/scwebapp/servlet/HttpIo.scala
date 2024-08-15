package scwebapp.servlet

import java.util.concurrent.atomic.AtomicBoolean

import jakarta.servlet.*
import jakarta.servlet.http.*

import scutil.lang.*

import scwebapp.*

object HttpIo {
	def execute(servletRequest:HttpServletRequest, servletResponse:HttpServletResponse, handler:HttpHandler):Unit	= {
		val request		= readRequest(servletRequest)
		val responder	= handler(request)
		responder match {
			case HttpResponder.Sync(response)	=>
				writeResponse(response, servletResponse)
			case HttpResponder.Async(responseCont, timeout, timeoutResponse, errorResponse)	=>
				typed[(HttpResponse=>Unit)=>Unit](responseCont)

				val asyncCtx	= servletRequest.startAsync()
				asyncCtx.setTimeout(timeout.millis)

				val alive	= new AtomicBoolean(true)

				def completeWith(response:HttpResponse):Unit	= {
					if (alive.compareAndSet(true, false)) {
						// TODO this seems to be called from onTimeout in rumms
						// and fail because the client connection is closed already.
						// weird, because the client supposedly never closes
						// the connection _before_ the timeout there
						writeResponse(response, servletResponse)
						asyncCtx.complete()
					}
				}
				asyncCtx.addListener(
					new AsyncListener {
						def onStartAsync(ev:AsyncEvent):Unit	= {}
						def onComplete(ev:AsyncEvent):Unit		= { alive.set(false)	}
						def onTimeout(ev:AsyncEvent):Unit		= { completeWith(timeoutResponse())	}
						def onError(ev:AsyncEvent):Unit			= { completeWith(errorResponse())	}
					}
				)
				responseCont(completeWith)
		}
	}

	private def readRequest(servletRequest:HttpServletRequest):HttpRequest	=
		new HttpRequestImpl(servletRequest)

	private def writeResponse(response:HttpResponse, servletResponse:HttpServletResponse):Unit	= {
		response.reason match {
			case Some(reason)	=> servletResponse.sendError(response.status.id, reason)
			case None			=> servletResponse.setStatus(response.status.id)
		}

		response.headers.foreach { case HeaderValue(k, v) =>
			servletResponse.addHeader(k, v)
		}

		response.body.intoOutputStream(servletResponse.getOutputStream)
	}
}
