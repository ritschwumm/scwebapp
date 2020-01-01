package scwebapp.header

import scwebapp.HeaderType

// TODO not typesafe

object Pragma extends HeaderType[Pragma] {
	val key	= "Pragma"

	def parse(it:String):Option[Pragma]	=
		Some(Pragma(it))

	def unparse(it:Pragma):String	=
		it.value
}

final case class Pragma(value:String)
