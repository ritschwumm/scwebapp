package scwebapp

import scutil.base.implicits._
import scutil.lang._
import scutil.time._

import scwebapp.status._

object HttpResponder {
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

	/*
	def sync2(response:HttpResponse):HttpResponder	= {
		val channel	= new Channel[HttpResponse]
		channel put response
		HttpResponderAsync(
			channel.get,
			MilliDuration.day,
			timeoutResponse,
			errorResponse
		)
	}
	*/

	private val timeoutResponse	= thunk { statusResponse(REQUEST_TIMEOUT)		}
	private val errorResponse	= thunk { statusResponse(INTERNAL_SERVER_ERROR)	}
	private def statusResponse(status:HttpStatus):HttpResponse	=
		HttpResponse(
			status, None,
			Vector.empty,
			HttpOutput.empty
		)

	//------------------------------------------------------------------------------

	final case class Sync(
		response:HttpResponse
	)
	extends HttpResponder {
		def modify(func:Endo[HttpResponse]):HttpResponder	= Sync(func(response))
	}

	final case class Async(
		response:Effect[Effect[HttpResponse]],
		timeout:MilliDuration,
		timeoutResponse:Thunk[HttpResponse],
		errorResponse:Thunk[HttpResponse]
	)
	extends HttpResponder {
		def modify(func:Endo[HttpResponse]):HttpResponder	=
			copy(response	= _ compose func into response)
	}
}

sealed trait HttpResponder {
	def modify(func:Endo[HttpResponse]):HttpResponder
}
