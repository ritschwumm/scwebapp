package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

// TODO only handles basic authentication

// @see http://www.ietf.org/rfc/rfc2617.txt
object Authorization extends HeaderType[Authorization] {
	val key	= "Authorization"

	def parse(it:String):Option[Authorization]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:Authorization):String	=
		BasicAuthorization.unparse(it.value)

	private object parsers {
		import HttpParsers.*

		val value:TextParser[Authorization]		= BasicAuthorization.parser.map(Authorization.apply)
		val finished:TextParser[Authorization]	= value.finishRight(LWSP)
	}
}

final case class Authorization(value:BasicAuthorization)
