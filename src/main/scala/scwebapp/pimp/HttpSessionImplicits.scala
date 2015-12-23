package scwebapp
package pimp

import javax.servlet.http._

object HttpSessionImplicits extends HttpSessionImplicits

trait HttpSessionImplicits {
	implicit def extendHttpSession(peer:HttpSession):HttpSessionExtension	=
			new HttpSessionExtension(peer)
}

final class HttpSessionExtension(peer:HttpSession) {
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			new HttpAttribute[T](
				getter	= ()	=> (peer getAttribute name).asInstanceOf[T],
				setter	= t		=> peer setAttribute (name, t),
				remover	= ()	=> peer removeAttribute name
			)
}
