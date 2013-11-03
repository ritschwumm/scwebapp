package scwebapp
package pimp

object HttpResponderImplicits extends HttpResponderImplicits

trait HttpResponderImplicits {
	implicit def extendHttpResponder(peer:HttpResponder):HttpResponderExt	= 
			new HttpResponderExt(peer)
	
	final class HttpResponderExt(peer:HttpResponder) {
		def ~>(that:HttpResponder):HttpResponder	= 
				response => { peer(response); that(response) } 
	}
}
