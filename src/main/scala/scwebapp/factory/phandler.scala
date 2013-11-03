package scwebapp
package factory

import scutil.lang._

object phandler extends phandler 

trait phandler {
	def PRespond(responder:HttpResponder):HttpPHandler	=
			constant(Some(responder))
		
	val Reject:HttpPHandler	= 
			constant(None)
}
