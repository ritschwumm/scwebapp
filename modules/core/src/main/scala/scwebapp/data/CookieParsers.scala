package scwebapp.data

import scwebapp.format.*
import scparse.ng.text.*

object CookieParsers {
	import HttpParsers.*

	private lazy val EQ	= TextParser.is('=')

	lazy val cookie_pair:TextParser[(String,String)]	= cookie_name left EQ next cookie_value
	lazy val cookie_name:TextParser[String]				= token
	lazy val cookie_value:TextParser[String]			= (cookie_octet.seq orElse (cookie_octet.seq within DQUOTE)).stringify
	lazy val cookie_octet:TextParser[Char]	=
		TextParser.is(0x21)				orElse
		TextParser.anyIn(0x23, 0x2b)	orElse
		TextParser.anyIn(0x2d, 0x3a)	orElse
		TextParser.anyIn(0x3c, 0x5b)	orElse
		TextParser.anyIn(0x5d, 0x7e)
}
