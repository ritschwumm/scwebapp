package scwebapp
package syntax

import javax.servlet.http._

import scutil.Functions._
import scutil.Implicits._

object HttpHandlerImplicits extends HttpHandlerImplicits

trait HttpHandlerImplicits {
	implicit def extendHttpHandler(delegate:HttpHandler):HttpHandlerExt	= 
			new HttpHandlerExt(delegate)
	
	final class HttpHandlerExt(delegate:HttpHandler) {
		def toChance:HttpChance	= 
				request	=> Some(delegate(request))
	}
}
