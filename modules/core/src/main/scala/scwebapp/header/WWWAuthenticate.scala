package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scparse.ng.text._

object WWWAuthenticate extends HeaderType[WWWAuthenticate] {
	val key	= "WWW-Authenticate"

	def parse(it:String):Option[WWWAuthenticate]	=
			parsers.finished.parseString(it).toOption

	def unparse(it:WWWAuthenticate):String	=
			BasicAuthenticate unparse it.value

	private object parsers {
		import HttpParsers._

		val value:TextParser[WWWAuthenticate]		= BasicAuthenticate.parser map WWWAuthenticate.apply
		val finished:TextParser[WWWAuthenticate]	= value finish LWSP
	}
}

final case class WWWAuthenticate(value:BasicAuthenticate)
