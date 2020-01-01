package scwebapp.header

import scutil.base.implicits._
import scutil.lang._

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scparse.ng.text._

object Range extends HeaderType[Range] {
	val key	= "Range"

	def parse(it:String):Option[Range]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:Range):String	=
		RangeType.keys.bytes + "=" +
		(it.patterns.toVector map RangePattern.unparse mkString ",")

	private object parsers {
		import HttpParsers._

		val bytesUnit:TextParser[String]				= symbolN(RangeType.keys.bytes)
		val byteRangeSet:TextParser[Nes[RangePattern]]	= hash1(RangePattern.parser)
		val rangePatterns:TextParser[Nes[RangePattern]]	= bytesUnit right symbol('=') right byteRangeSet

		val value:TextParser[Range]		= rangePatterns map Range.apply
		val finished:TextParser[Range]	= value finish LWSP
	}
}

final case class Range(patterns:Nes[RangePattern]) {
	def inclusiveRanges(total:Long):Seq[InclusiveRange]	=
		patterns.toVector collapseMap { _ toInclusiveRange total }
}
