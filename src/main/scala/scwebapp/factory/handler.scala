package scwebapp
package factory

import scutil.lang._

object handler extends handler

trait handler {
	def Respond(responder:HttpResponder):HttpHandler	=
			constant(responder)
}
