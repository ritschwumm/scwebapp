package scwebapp

import scutil.core.implicits._
import scutil.lang._
import scutil.time._

import scwebapp.status._

object HttpResponder {
	@deprecated("use HttpResponder.sync", "0.258.0")
	def apply(response:HttpResponse):HttpResponder	=
		sync(response)

	def sync(response:HttpResponse):HttpResponder	=
		Sync(response)

	def async(
		timeout:MilliDuration,
		timeoutResponse:Thunk[HttpResponse]	= timeoutResponse,
		errorResponse:Thunk[HttpResponse]	= errorResponse
	):(HttpResponder, Effect[HttpResponse])	= {
		val channel	= new Channel[HttpResponse]
		val resp	=
			Async(
				channel.get,
				timeout,
				timeoutResponse,
				errorResponse
			)
		(resp, channel.put)
	}

	private val timeoutResponse	= thunk { statusResponse(REQUEST_TIMEOUT)		}
	private val errorResponse	= thunk { statusResponse(INTERNAL_SERVER_ERROR)	}
	private def statusResponse(status:HttpStatus):HttpResponse	=
		HttpResponse(
			status, None,
			Vector.empty,
			HttpOutput.empty
		)

	//------------------------------------------------------------------------------

	private[scwebapp] final case class Sync(
		response:HttpResponse
	)
	extends HttpResponder {
		def modify(func:HttpResponse=>HttpResponse):HttpResponder	= Sync(func(response))
	}

	private[scwebapp] final case class Async(
		response:Effect[Effect[HttpResponse]],
		timeout:MilliDuration,
		timeoutResponse:Thunk[HttpResponse],
		errorResponse:Thunk[HttpResponse]
	)
	extends HttpResponder {
		def modify(func:HttpResponse=>HttpResponse):HttpResponder	=
			copy(response	= _ compose func into response)
	}
}

sealed trait HttpResponder {
	def modify(func:HttpResponse=>HttpResponse):HttpResponder
}
