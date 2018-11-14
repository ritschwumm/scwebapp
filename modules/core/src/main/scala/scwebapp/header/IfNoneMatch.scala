package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._

object IfNoneMatch extends HeaderType[IfNoneMatch] {
	val key	= "If-None-Match"

	def parse(it:String):Option[IfNoneMatch]	=
			MatchValue parse it map IfNoneMatch.apply

	def unparse(it:IfNoneMatch):String	=
			MatchValue unparse it.value
}

final case class IfNoneMatch(value:MatchValue) {
	def matches(it:ETagValue):Boolean	= value matches it
}
