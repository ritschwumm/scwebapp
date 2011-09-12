import javax.servlet.http._

import scutil.Types._

package object scwebapp {
	type HttpResponder	= Effect[HttpServletResponse]
	
	type HttpHandler	= HttpServletRequest => HttpResponder
	type HttpChance		= HttpServletRequest => Option[HttpResponder]
				
	type HttpCondition	= Predicate[HttpServletRequest]
	type HttpRoute		= (HttpCondition, HttpHandler)
	
	// implicit def ResponderToHandler(responder:HttpResponder):HttpChance		= _ => Some(responder)
	// implicit def StatusToResponder(status:HttpStatusCode):HttpResponder		= _ setStatus status
}
