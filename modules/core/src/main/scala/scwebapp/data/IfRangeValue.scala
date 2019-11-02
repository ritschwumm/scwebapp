package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object IfRangeValue {
	lazy val parser:CParser[IfRangeValue]	= parsers.value

	def unparse(it:IfRangeValue):String	=
			it match {
				case IsHttpDate(x)	=> HttpDate unparse x
				case EntityTag(x)	=> ETagValue unparse x
			}

	private object parsers {
		import HttpParsers._

		val date:CParser[IfRangeValue]	= dateValue map IsHttpDate.apply
		val etag:CParser[IfRangeValue]	= ETagValue.parser map EntityTag.apply

		val value:CParser[IfRangeValue]	= date orElse etag
	}

	//------------------------------------------------------------------------------

	final case class IsHttpDate(value:HttpDate)	extends IfRangeValue
	final case class EntityTag(value:ETagValue)	extends IfRangeValue
}

sealed trait IfRangeValue {
	// TODO why add a second here?
	def needsFull(eTag:ETagValue, lastModified:HttpDate):Boolean	=
			this match {
				case IfRangeValue.IsHttpDate(x)	=> x + HttpDuration.second < lastModified
				case IfRangeValue.EntityTag(x)	=> x != eTag
			}
}
