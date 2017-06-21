package scwebapp.servlet.bootstrap

import scutil.lang._

object Property {
	def lens[C,V](key:String, lens:TLens[C,V], parse:String=>Either[String,V]):Property[C]	=
			Property(
				key		= key,
				mod		= raw => parse(raw) map (lens.putter),
				visible	= true
			)
}
final case class Property[C](key:String, mod:String=>Either[String,Endo[C]], visible:Boolean) {
	def hidden:Property[C]	= copy(visible = false)
}
