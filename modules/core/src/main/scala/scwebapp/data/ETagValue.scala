package scwebapp.data

import scutil.base.implicits._

import scwebapp.format._
import scparse.ng.text._

object ETagValue {
	lazy val parser:TextParser[ETagValue]	= parsers.value

	def unparse(it:ETagValue):String	=
		(it.weak cata ("", "W/")) +
		(HttpUnparsers quotedString it.value)

	private object parsers {
		import HttpParsers._

		val weak:TextParser[Boolean]		= symbolN("W/").flag
		val value:TextParser[ETagValue]	= weak next quotedString map (ETagValue.apply _).tupled
	}
}

// TODO restrict characters
// TODO add matching weak/strong here
final case class ETagValue(weak:Boolean, value:String)
