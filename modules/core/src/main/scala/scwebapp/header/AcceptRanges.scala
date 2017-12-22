package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object AcceptRanges extends HeaderType[AcceptRanges] {
	val key	= "Accept-Ranges"
	
	def parse(it:String):Option[AcceptRanges]	=
			parsers.finished parseStringOption it
		
	def unparse(it:AcceptRanges):String	=
			RangeType unparse it.rangeType
		
	private object parsers {
		import HttpParsers._
		
		val value:CParser[AcceptRanges]		= RangeType.parser map AcceptRanges.apply
		val finished:CParser[AcceptRanges]	= value finish LWSP
	}
}

final case class AcceptRanges(rangeType:RangeType)
