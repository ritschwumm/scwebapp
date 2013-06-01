import javax.servlet.http._

import scutil.lang._

package object scwebapp {
	type HttpPredicate	= Predicate[HttpServletRequest]
	type HttpResponder	= Effect[HttpServletResponse]
	
	type HttpHandler	= HttpServletRequest => HttpResponder
	type HttpPHandler	= PFunction[HttpServletRequest,HttpResponder]
	
	// implicit def ResponderToHandler(responder:HttpResponder):HttpPHandler		= _ => Some(responder)
	// implicit def StatusToResponder(status:HttpStatus):HttpResponder		= _ setStatus status
}
