package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scparse.ng.text._

object AcceptRanges extends HeaderType[AcceptRanges] {
	val key	= "Accept-Ranges"

	def parse(it:String):Option[AcceptRanges]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:AcceptRanges):String	=
		RangeType unparse it.rangeType

	private object parsers {
		import HttpParsers._

		val value:TextParser[AcceptRanges]	= RangeType.parser map AcceptRanges.apply
		val finished:TextParser[AcceptRanges]	= value finish LWSP
	}
}

final case class AcceptRanges(rangeType:RangeType)
