import javax.servlet.http._

import scutil.lang._

import scwebapp.method._

package object scwebapp {
	//------------------------------------------------------------------------------
	//## base types
	
	type HttpPredicate	= Predicate[HttpServletRequest]
	type HttpResponder	= Effect[HttpServletResponse]
	
	type HttpHandler	= HttpServletRequest => HttpResponder
	type HttpPHandler	= PFunction[HttpServletRequest,HttpResponder]
	
	//------------------------------------------------------------------------------
	//## type helpers
	
	def HttpPredicate(it:HttpPredicate):HttpPredicate	= it
	def HttpResponder(it:HttpResponder):HttpResponder	= it
	
	def HttpHandler(it:HttpHandler):HttpHandler			= it
	def HttpPHandler(it:HttpPHandler):HttpPHandler		= it
	
	//------------------------------------------------------------------------------
	
	
	/*
	import scutil.lang._
	import implicits._
	import instances._
	
	implicit def MethodToPredicate(method:HttpMethod):HttpPredicate			= Method(method)
	implicit def ResponderToHandler(responder:HttpResponder):HttpHandler	= Respond(responder)
	implicit def ResponderToPHandler(responder:HttpResponder):HttpPHandler	= PRespond(responder)
	implicit def StatusToResponder(status:HttpStatus):HttpResponder			= SetStatus(status)
	*/
	
	val HttpMethods:ISeq[HttpMethod]	= 
			Vector (
				OPTIONS, HEAD, GET, POST, PUT, DELETE, TRACE, CONNECT,
				PROPFIND, PROPPATCH, MKCOL, COPY, MOVE, LOCK, UNLOCK
			)
}
