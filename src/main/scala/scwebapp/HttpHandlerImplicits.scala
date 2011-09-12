package scwebapp

import javax.servlet.http._

import scutil.Implicits._
import scutil.Types._

object HttpHandlerImplicits extends HttpHandlerImplicits

trait HttpHandlerImplicits {
	implicit def extendHttpHandler(delegate:HttpHandler):HttpHandlerExt	= 
			new HttpHandlerExt(delegate)
	
	final class HttpHandlerExt(delegate:HttpHandler) {
		def toChance:HttpChance	= 
				request	=> Some(delegate(request))
				
		def when(condition:HttpCondition):HttpChance	=
				condition guardOn delegate
						
		def unless(condition:HttpCondition):HttpChance	= 
				condition preventOn delegate
	}
}
