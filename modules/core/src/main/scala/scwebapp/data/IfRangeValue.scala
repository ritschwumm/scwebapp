package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object IfRangeValue {
	lazy val parser:CParser[IfRangeValue]	= parsers.value

	def unparse(it:IfRangeValue):String	=
			it match {
				case IfRangeHttpDate(x)		=> HttpDate unparse x
				case IfRangeEntityTag(x)	=> ETagValue unparse x
			}

	private object parsers {
		import HttpParsers._

		val date:CParser[IfRangeValue]	= dateValue map IfRangeHttpDate.apply
		val etag:CParser[IfRangeValue]	= ETagValue.parser map IfRangeEntityTag.apply

		val value:CParser[IfRangeValue]	= date orElse etag
	}
}

sealed trait IfRangeValue {
	// TODO why add a second here?
	def needsFull(eTag:ETagValue, lastModified:HttpDate):Boolean	=
			this match {
				case IfRangeHttpDate(x)		=> x + HttpDuration.second < lastModified
				case IfRangeEntityTag(x)	=> x != eTag
			}
}
final case class IfRangeHttpDate(value:HttpDate)	extends IfRangeValue
final case class IfRangeEntityTag(value:ETagValue)	extends IfRangeValue
