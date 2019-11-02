package scwebapp.data

import scutil.lang._

import scwebapp.format._
import scwebapp.parser.string._

object MatchValue {
	// NOTE this is special as we provide a complete tag
	def parse(it:String):Option[MatchValue]	=
			parsers.finished parseStringOption it

	def unparse(it:MatchValue):String	=
			it match {
				case Wildcard		=> "*"
				case EntityTags(x)	=> x.toVector map ETagValue.unparse mkString ","
			}

	private object parsers {
		import HttpParsers._

		val wildcardValue:CParser[MatchValue]	= symbol('*') tag Wildcard
		val etagsValue:CParser[MatchValue]		= hash1(ETagValue.parser) map EntityTags.apply

		val value:CParser[MatchValue]		= wildcardValue orElse etagsValue
		val finished:CParser[MatchValue]	= value finish LWSP
	}

	//------------------------------------------------------------------------------

	final case object Wildcard							extends MatchValue
	final case class EntityTags(values:Nes[ETagValue])	extends MatchValue
}

sealed trait MatchValue {
	def matches(it:ETagValue):Boolean	=
			this match {
				case MatchValue.Wildcard		=> true
				case MatchValue.EntityTags(xs)	=> xs.toVector.toSet contains it
			}
}
