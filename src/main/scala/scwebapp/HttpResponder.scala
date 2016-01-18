package scwebapp

import scutil.lang._
import scutil.time._

import scwebapp.status._

object HttpResponder {
	def apply(response:HttpResponse):HttpResponder	=
			sync(response)
	
	def sync(response:HttpResponse):HttpResponder	=
			HttpResponderSync(response)
	
	def async(
		timeout:MilliDuration,
		timeoutResponse:Thunk[HttpResponse]	= timeoutResponse,
		errorResponse:Thunk[HttpResponse]	= errorResponse
	):(HttpResponder, Effect[HttpResponse])	= {
		val channel	= new Channel[HttpResponse]
		val resp	=
				HttpResponderAsync(
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
}

sealed trait HttpResponder

final case class HttpResponderSync(
	response:HttpResponse
)
extends HttpResponder

final case class HttpResponderAsync(
	response:Effect[Effect[HttpResponse]],
	timeout:MilliDuration,
	timeoutResponse:Thunk[HttpResponse],
	errorResponse:Thunk[HttpResponse]
)
extends HttpResponder
