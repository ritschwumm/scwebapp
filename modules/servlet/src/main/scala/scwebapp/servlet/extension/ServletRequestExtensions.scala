package scwebapp.servlet.extension

import jakarta.servlet.*

import scwebapp.servlet.HttpAttribute

object ServletRequestExtensions {
	extension(peer:ServletRequest) {
		def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			new HttpAttribute[T](
				()		=> peer.getAttribute(name),
				(it)	=> peer.setAttribute(name, it),
				()		=> peer.removeAttribute(name),
			)
	}
}
