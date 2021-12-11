package scwebapp.header

import scutil.core.implicits.*
import scutil.lang.*

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object Range extends HeaderType[Range] {
	val key	= "Range"

	def parse(it:String):Option[Range]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:Range):String	=
		RangeType.keys.bytes + "=" +
		(it.patterns.toVector map RangePattern.unparse mkString ",")

	private object parsers {
		import HttpParsers.*

		val bytesUnit:TextParser[String]				= symbolN(RangeType.keys.bytes)
		val byteRangeSet:TextParser[Nes[RangePattern]]	= hash1(RangePattern.parser)
		val rangePatterns:TextParser[Nes[RangePattern]]	= bytesUnit right symbol('=') right byteRangeSet

		val value:TextParser[Range]		= rangePatterns map Range.apply
		val finished:TextParser[Range]	= value finishRight LWSP
	}
}

final case class Range(patterns:Nes[RangePattern]) {
	def inclusiveRanges(total:Long):Seq[InclusiveRange]	=
		patterns.toVector mapFilter { _ toInclusiveRange total }
}
