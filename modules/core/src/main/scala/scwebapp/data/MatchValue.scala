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
				case MatchWildcard		=> "*"
				case MatchEntityTags(x)	=> x.toVector map ETagValue.unparse mkString ","
			}

	private object parsers {
		import HttpParsers._

		val wildcardValue:CParser[MatchValue]	= symbol('*') tag MatchWildcard
		val etagsValue:CParser[MatchValue]		= hash1(ETagValue.parser) map MatchEntityTags.apply

		val value:CParser[MatchValue]		= wildcardValue orElse etagsValue
		val finished:CParser[MatchValue]	= value finish LWSP
	}
}

sealed trait MatchValue {
	def matches(it:ETagValue):Boolean	=
			this match {
				case MatchWildcard			=> true
				case MatchEntityTags(xs)	=> xs.toVector.toSet contains it
			}
}

final case object MatchWildcard							extends MatchValue
final case class MatchEntityTags(values:Nes[ETagValue])	extends MatchValue
