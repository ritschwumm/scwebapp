import javax.servlet.http._

import scutil.lang._

package object scwebapp {
	type HttpPredicate	= Predicate[HttpServletRequest]
	type HttpResponder	= Effect[HttpServletResponse]
	
	type HttpHandler	= HttpServletRequest => HttpResponder
	type HttpChance		= Chance[HttpServletRequest,HttpResponder]
	
	// implicit def ResponderToHandler(responder:HttpResponder):HttpChance		= _ => Some(responder)
	// implicit def StatusToResponder(status:HttpStatus):HttpResponder		= _ setStatus status
}
