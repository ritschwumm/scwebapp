package scwebapp.data

import scutil.implicits._

import scwebapp.format._
import scwebapp.parser.string._

object ETagValue {
	lazy val parser:CParser[ETagValue]	= parsers.value
		
	def unparse(it:ETagValue):String	=
			(it.weak cata ("", "W/")) +
			(HttpUnparsers quotedString it.value)
		
	private object parsers {
		import HttpParsers._
		
		val weak:CParser[Boolean]		= symbolN("W/").flag
		val value:CParser[ETagValue]	= weak next quotedString map (ETagValue.apply _).tupled
	}
}

// TODO restrict characters
// TODO add matching weak/strong here
final case class ETagValue(weak:Boolean, value:String)
