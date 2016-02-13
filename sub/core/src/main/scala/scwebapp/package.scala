import scutil.lang._

package object scwebapp {
	type HttpPredicate	= Predicate[HttpRequest]
	type HttpHandler	= HttpRequest => HttpResponder
	type HttpPHandler	= PFunction[HttpRequest,HttpResponder]
	
	def HttpPredicate(it:HttpPredicate):HttpPredicate	= it
	def HttpHandler(it:HttpHandler):HttpHandler			= it
	def HttpPHandler(it:HttpPHandler):HttpPHandler		= it
}
