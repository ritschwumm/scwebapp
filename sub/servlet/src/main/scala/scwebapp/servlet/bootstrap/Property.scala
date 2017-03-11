package scwebapp.servlet.bootstrap

import scutil.lang._

object Property {
	def lens[C,V](key:String, lens:TLens[C,V], parse:String=>Tried[String,V]):Property[C]	=
			Property(
				key, 
				raw => parse(raw) map (lens.putter)
			)
}
final case class Property[C](key:String, mod:String=>Tried[String,Endo[C]])
