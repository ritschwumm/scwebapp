package scwebapp
package syntax

import javax.servlet.http._

import scutil.lang._
import scutil.Implicits._

object HttpPHandlerImplicits extends HttpPHandlerImplicits

trait HttpPHandlerImplicits {
	implicit def extendHttpPHandler(delegate:HttpPHandler):HttpPHandlerExt	= 
			new HttpPHandlerExt(delegate)
	
	final class HttpPHandlerExt(delegate:HttpPHandler) {
		def orAlways(that:HttpHandler):HttpHandler	=
				request	=> delegate(request) getOrElse that(request)
						
		def orElse(that:HttpPHandler):HttpPHandler	= 
				request	=> delegate(request) orElse that(request)
	}
}
