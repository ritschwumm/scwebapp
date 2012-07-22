package scwebapp
package api

import javax.servlet.http._

object HttpSessionImplicits extends HttpSessionImplicits

trait HttpSessionImplicits {
	implicit def extendHttpSession(delegate:HttpSession):HttpSessionExtension		= 
			new HttpSessionExtension(delegate)
}

final class HttpSessionExtension(delegate:HttpSession) {
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	= 
			new HttpAttribute[T](
					()	=> (delegate getAttribute name).asInstanceOf[T],
					t	=> delegate setAttribute (name, t),
					()	=> delegate removeAttribute name)
}
