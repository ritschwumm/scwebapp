package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scparse.ng.text._

object Expires extends HeaderType[Expires] {
	val key	= "Expires"

	def parse(it:String):Option[Expires]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:Expires):String	=
		HttpDate unparse it.value

	private object parsers {
		import HttpParsers._

		val value:TextParser[Expires]		= dateValue map Expires.apply
		val finished:TextParser[Expires]	= value finish LWSP
	}
}

final case class Expires(value:HttpDate)
