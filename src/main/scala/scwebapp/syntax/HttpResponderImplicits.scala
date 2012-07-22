package scwebapp
package syntax

object HttpResponderImplicits extends HttpResponderImplicits

trait HttpResponderImplicits {
	implicit def extendHttpResponder(delegate:HttpResponder):HttpResponderExt	= 
			new HttpResponderExt(delegate)
	
	final class HttpResponderExt(delegate:HttpResponder) {
		def ~>(that:HttpResponder):HttpResponder	= 
				response => { delegate(response); that(response) } 
	}
}
