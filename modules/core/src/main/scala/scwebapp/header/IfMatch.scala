package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*

object IfMatch extends HeaderType[IfMatch] {
	val key	= "If-Match"

	def parse(it:String):Option[IfMatch]	=
		MatchValue.parse(it).map(IfMatch.apply)

	def unparse(it:IfMatch):String	=
		MatchValue.unparse(it.value)
}

final case class IfMatch(value:MatchValue) {
	def matches(it:ETagValue):Boolean	= value.matches(it)
}
