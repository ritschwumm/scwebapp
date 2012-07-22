import javax.servlet.http._

import scutil.Functions._

package object scwebapp {
	type HttpPredicate	= Predicate[HttpServletRequest]
	type HttpResponder	= Effect[HttpServletResponse]
	
	type HttpHandler	= HttpServletRequest => HttpResponder
	type HttpChance		= HttpServletRequest => Option[HttpResponder]
	
	// implicit def ResponderToHandler(responder:HttpResponder):HttpChance		= _ => Some(responder)
	// implicit def StatusToResponder(status:HttpStatus):HttpResponder		= _ setStatus status
}
