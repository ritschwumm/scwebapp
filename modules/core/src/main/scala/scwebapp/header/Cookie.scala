package scwebapp.header

import scutil.lang._

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object Cookie extends HeaderType[Cookie] {
	val key	= "Cookie"

	def parse(it:String):Option[Cookie]	=
			parsers.finished parseStringOption it

	def unparse(it:Cookie):String	=
			HttpUnparsers parameters it.values

	private object parsers {
		import HttpParsers._
		import CookieParsers._

		val cookie_string:CParser[ISeq[(String,String)]]	= cookie_pair sepSeq (cis(';') next SP)

		// TODO inside OWS is stupid. revise whitespace handling.
		val cookieParams:CParser[CaseParameters]	= cookie_string inside OWS map CaseParameters.apply

		val value:CParser[Cookie]		= cookieParams map Cookie.apply
		val finished:CParser[Cookie]	= value finish LWSP
	}
}

// TODO restrict allowed characters
final case class Cookie(values:CaseParameters)
