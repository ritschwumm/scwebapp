package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object CookieParsers {
	import HttpParsers._
	
	lazy val cookie_pair:CParser[(String,String)]	= cookie_name left cis('=') next cookie_value
	lazy val cookie_name:CParser[String]			= token
	lazy val cookie_value:CParser[String]			= (cookie_octet.seq orElse (cookie_octet.seq inside DQUOTE)).stringify
	lazy val cookie_octet:CParser[Char]				=
			cis(0x21)		orElse
			rng(0x23, 0x2b)	orElse
			rng(0x2d, 0x3a)	orElse
			rng(0x3c, 0x5b)	orElse
			rng(0x5d, 0x7e)
}
