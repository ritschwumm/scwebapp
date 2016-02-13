package scwebapp.header

import scutil.lang._

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object Range extends HeaderType[Range] {
	val key	= "Range"
	
	def parse(it:String):Option[Range]	=
			parsers.finished parseStringOption it
		
	def unparse(it:Range):String	=
			RangeType.keys.bytes + "=" +
			(it.patterns.toVector map RangePattern.unparse mkString ",")
			
	private object parsers {
		import HttpParsers._
		
		val bytesUnit:CParser[String]					= symbolN(RangeType.keys.bytes)
		val byteRangeSet:CParser[Nes[RangePattern]]		= hash1(RangePattern.parser)
		val rangePatterns:CParser[Nes[RangePattern]]	= bytesUnit right symbol('=') right byteRangeSet
			
		val value:CParser[Range]	= rangePatterns map Range.apply
		val finished:CParser[Range]	= value finish LWSP
	}
}

final case class Range(patterns:Nes[RangePattern]) {
	def inclusiveRanges(total:Long):ISeq[InclusiveRange]	=
			patterns.toVector flatMap { _ toInclusiveRange total }
}
