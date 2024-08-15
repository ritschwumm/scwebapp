package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object Expires extends HeaderType[Expires] {
	val key	= "Expires"

	def parse(it:String):Option[Expires]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:Expires):String	=
		HttpDate.unparse(it.value)

	private object parsers {
		import HttpParsers.*

		val value:TextParser[Expires]		= dateValue.map(Expires.apply)
		val finished:TextParser[Expires]	= value.finishRight(LWSP)
	}
}

final case class Expires(value:HttpDate)
