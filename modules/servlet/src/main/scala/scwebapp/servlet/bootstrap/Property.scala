package scwebapp.servlet.bootstrap

import scutil.lang.*

object Property {
	def lens[C,V](key:String, lens:Lens[C,V], parse:String=>Either[String,V]):Property[C]	=
		Property(
			key		= key,
			mod		= raw => parse(raw).map(lens.set),
			visible	= true
		)
}
final case class Property[C](key:String, mod:String=>Either[String,C=>C], visible:Boolean) {
	def hidden:Property[C]	= copy(visible = false)
}
