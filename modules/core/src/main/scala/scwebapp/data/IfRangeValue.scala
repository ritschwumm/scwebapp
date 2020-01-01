package scwebapp.data

import scwebapp.format._
import scparse.ng.text._

object IfRangeValue {
	lazy val parser:TextParser[IfRangeValue]	= parsers.value

	def unparse(it:IfRangeValue):String	=
			it match {
				case IsHttpDate(x)	=> HttpDate unparse x
				case EntityTag(x)	=> ETagValue unparse x
			}

	private object parsers {
		import HttpParsers._

		val date:TextParser[IfRangeValue]		= dateValue map IsHttpDate.apply
		val etag:TextParser[IfRangeValue]		= ETagValue.parser map EntityTag.apply

		val value:TextParser[IfRangeValue]	= date orElse etag
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
