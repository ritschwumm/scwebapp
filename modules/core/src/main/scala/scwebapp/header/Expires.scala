package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object Expires extends HeaderType[Expires] {
	val key	= "Expires"

	def parse(it:String):Option[Expires]	=
			parsers.finished parseStringOption it

	def unparse(it:Expires):String	=
			HttpDate unparse it.value

	private object parsers {
		import HttpParsers._

		val value:CParser[Expires]		= dateValue map Expires.apply
		val finished:CParser[Expires]	= value finish LWSP
	}
}

final case class Expires(value:HttpDate)
