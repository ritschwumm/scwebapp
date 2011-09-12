package scwebapp

import scutil.Functions._

object HttpHandler {
	def Respond(responder:HttpResponder):HttpHandler	=
			constant(responder)
}
