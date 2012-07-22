package scwebapp
package syntax

import javax.servlet.http._

import scutil.lang._
import scutil.Implicits._

object HttpChanceImplicits extends HttpChanceImplicits

trait HttpChanceImplicits {
	implicit def extendHttpChance(delegate:HttpChance):HttpChanceExt	= 
			new HttpChanceExt(delegate)
	
	final class HttpChanceExt(delegate:HttpChance) {
		def orAlways(that:HttpHandler):HttpHandler	=
				request	=> delegate(request) getOrElse that(request)
						
		def orElse(that:HttpChance):HttpChance	= 
				request	=> delegate(request) orElse that(request)
	}
}
