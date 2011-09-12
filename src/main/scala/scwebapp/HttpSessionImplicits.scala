package scwebapp

import javax.servlet.http._

object HttpSessionImplicits extends HttpSessionImplicits

trait HttpSessionImplicits {
	implicit def extendHttpSession(delegate:HttpSession):HttpSessionExtension		= 
			new HttpSessionExtension(delegate)
}

final class HttpSessionExtension(delegate:HttpSession) {
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			HttpAttribute session (delegate, name)
}
