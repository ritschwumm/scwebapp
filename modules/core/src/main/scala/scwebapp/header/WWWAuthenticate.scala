package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object WWWAuthenticate extends HeaderType[WWWAuthenticate] {
	val key	= "WWW-Authenticate"

	def parse(it:String):Option[WWWAuthenticate]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:WWWAuthenticate):String	=
		BasicAuthenticate.unparse(it.value)

	private object parsers {
		import HttpParsers.*

		val value:TextParser[WWWAuthenticate]		= BasicAuthenticate.parser.map(WWWAuthenticate.apply)
		val finished:TextParser[WWWAuthenticate]	= value.finishRight(LWSP)
	}
}

final case class WWWAuthenticate(value:BasicAuthenticate)
