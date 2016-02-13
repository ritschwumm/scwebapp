package scwebapp.data

import scutil.implicits._

import scwebapp.format._
import scwebapp.parser.string._

object RangePattern {
	lazy val parser:CParser[RangePattern]	=
			parsers.value
		
	def unparse(it:RangePattern):String	=
			it match {
				case RangeBegin(s)		=> s + "-"
				case RangeEnd(c)		=> "-" + c
				case RangeFromTo(s,e)	=> s + "-" + e
			}
			
	private object parsers {
		import HttpParsers._
		
		val bytePos:CParser[Long]						= DIGIT.nes.stringify map { _.toLong } eating LWSP
		val byteRangeSpec:CParser[(Long,Option[Long])]	= bytePos left symbol('-') next bytePos.option
		val suffixByteRangeSpec:CParser[Long]			= symbol('-') right bytePos
		val value:CParser[RangePattern]			=
				byteRangeSpec either suffixByteRangeSpec map {
					case Left((a, None))	=> RangeBegin(a)
					case Left((a, Some(b)))	=> RangeFromTo(a, b)
					case Right(b)			=> RangeEnd(b)
				}
	}
}

sealed trait RangePattern {
	def toInclusiveRange(total:Long):Option[InclusiveRange]	= {
		val last	= total - 1
		this matchOption {
			case RangeFromTo(start, end)	if start >= 0 && start <= last && end < last	=> InclusiveRange(start,			end)
			case RangeBegin(start)			if start >= 0 && start <= last					=> InclusiveRange(start,			last)
			case RangeEnd(count)			if count > 0  && count <= total					=> InclusiveRange(total - count,	last)
		}
	}
			
}
final case class RangeBegin(start:Long)				extends RangePattern
final case class RangeFromTo(start:Long, end:Long)	extends RangePattern
final case class RangeEnd(size:Long)				extends RangePattern
