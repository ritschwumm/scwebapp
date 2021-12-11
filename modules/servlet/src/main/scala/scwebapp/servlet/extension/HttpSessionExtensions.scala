package scwebapp.servlet.extension

import jakarta.servlet.http.*

import scwebapp.servlet.HttpAttribute

object HttpSessionExtensions {
	extension(peer:HttpSession) {
		def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			new HttpAttribute[T](
				()		=> peer.getAttribute(name),
				(it)	=> peer.setAttribute(name, it),
				()		=> peer.removeAttribute(name),
			)
	}
}
