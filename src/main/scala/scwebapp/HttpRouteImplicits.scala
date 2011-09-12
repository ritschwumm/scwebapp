package scwebapp

import HttpHandlerImplicits._

object HttpRouteImplicits extends HttpRouteImplicits

trait HttpRouteImplicits {
	implicit def extendHttpRoute(delegate:HttpRoute):HttpRouteExt	= 
			new HttpRouteExt(delegate)
	
	final class HttpRouteExt(delegate:HttpRoute) {
		def toChance:HttpChance	= 
				delegate._2 when delegate._1
	}
}
