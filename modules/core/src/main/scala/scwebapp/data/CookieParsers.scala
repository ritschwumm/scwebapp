package scwebapp.data

import scwebapp.format._
import scparse.ng.text._

object CookieParsers {
	import HttpParsers._

	lazy val cookie_pair:TextParser[(String,String)]	= cookie_name left TextParser.isChar('=') next cookie_value
	lazy val cookie_name:TextParser[String]				= token
	lazy val cookie_value:TextParser[String]			= (cookie_octet.seq orElse (cookie_octet.seq within DQUOTE)).stringify
	lazy val cookie_octet:TextParser[Char]	=
		TextParser.isChar(0x21)					orElse
		TextParser.anyCharInRange(0x23, 0x2b)	orElse
		TextParser.anyCharInRange(0x2d, 0x3a)	orElse
		TextParser.anyCharInRange(0x3c, 0x5b)	orElse
		TextParser.anyCharInRange(0x5d, 0x7e)
}
