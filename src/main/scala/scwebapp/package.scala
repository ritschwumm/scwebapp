import javax.servlet.http._

import scutil.lang._

package object scwebapp {
	type HttpPredicate	= Predicate[HttpServletRequest]
	type HttpResponder	= Effect[HttpServletResponse]
	
	type HttpHandler	= HttpServletRequest => HttpResponder
	type HttpPHandler	= PFunction[HttpServletRequest,HttpResponder]
	
	/*
	import scutil.lang._
	import implicits._
	import instances._
	
	implicit def MethodToPredicate(method:HttpMethod):HttpPredicate			= Method(method)
	implicit def ResponderToHandler(responder:HttpResponder):HttpHandler	= Respond(responder)
	implicit def ResponderToPHandler(responder:HttpResponder):HttpPHandler	= PRespond(responder)
	implicit def StatusToResponder(status:HttpStatus):HttpResponder			= SetStatus(status)
	*/
}
