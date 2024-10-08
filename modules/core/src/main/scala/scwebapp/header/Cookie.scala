package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object Cookie extends HeaderType[Cookie] {
	val key	= "Cookie"

	def parse(it:String):Option[Cookie]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:Cookie):String	=
		HttpUnparsers.parameters(it.values)

	private object parsers {
		import HttpParsers.*
		import CookieParsers.*

		val cookie_string:TextParser[Seq[(String,String)]]	= cookie_pair.seqSepBy(TextParser.is(';').next(SP))

		// TODO inside OWS is stupid. revise whitespace handling.
		val cookieParams:TextParser[CaseParameters]	= cookie_string.within(OWS).map(CaseParameters.apply)

		val value:TextParser[Cookie]	= cookieParams.map(Cookie.apply)
		val finished:TextParser[Cookie]	= value.finishRight(LWSP)
	}
}

// TODO restrict allowed characters
final case class Cookie(values:CaseParameters)
