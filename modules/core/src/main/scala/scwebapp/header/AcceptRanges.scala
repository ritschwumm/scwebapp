package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object AcceptRanges extends HeaderType[AcceptRanges] {
	val key	= "Accept-Ranges"

	def parse(it:String):Option[AcceptRanges]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:AcceptRanges):String	=
		RangeType unparse it.rangeType

	private object parsers {
		import HttpParsers.*

		val value:TextParser[AcceptRanges]		= RangeType.parser map AcceptRanges.apply
		val finished:TextParser[AcceptRanges]	= value finishRight LWSP
	}
}

final case class AcceptRanges(rangeType:RangeType)
