package scwebapp.data

import scutil.lang.*

import scwebapp.format.*
import scparse.ng.text.*

object MatchValue {
	// NOTE this is special as we provide a complete tag
	def parse(it:String):Option[MatchValue]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:MatchValue):String	=
		it match {
			case Wildcard		=> "*"
			case EntityTags(x)	=> x.toVector map ETagValue.unparse mkString ","
		}

	private object parsers {
		import HttpParsers.*

		val wildcardValue:TextParser[MatchValue]	= symbol('*') tag Wildcard
		val etagsValue:TextParser[MatchValue]		= hash1(ETagValue.parser) map EntityTags.apply

		val value:TextParser[MatchValue]	= wildcardValue orElse etagsValue
		val finished:TextParser[MatchValue]	= value finishRight LWSP
	}

	//------------------------------------------------------------------------------

	case object Wildcard								extends MatchValue
	final case class EntityTags(values:Nes[ETagValue])	extends MatchValue
}

sealed trait MatchValue {
	def matches(it:ETagValue):Boolean	=
		this match {
			case MatchValue.Wildcard		=> true
			case MatchValue.EntityTags(xs)	=> xs.toVector.toSet contains it
		}
}
