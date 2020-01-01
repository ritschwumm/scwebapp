package scwebapp.data

import scutil.base.implicits._

import scwebapp.format._
import scparse.ng.text._

object RangePattern {
	lazy val parser:TextParser[RangePattern]	=
			parsers.value

	def unparse(it:RangePattern):String	=
			it match {
				case Begin(s)		=> s.toString + "-"
				case End(c)		=> "-" + c.toString
				case FromTo(s,e)	=> s.toString + "-" + e.toString
			}

	private object parsers {
		import HttpParsers._

		val bytePos:TextParser[Long]						= DIGIT.nes.stringify map { _.toLong } eatLeft LWSP
		val byteRangeSpec:TextParser[(Long,Option[Long])]	= bytePos left symbol('-') next bytePos.option
		val suffixByteRangeSpec:TextParser[Long]			= symbol('-') right bytePos
		val value:TextParser[RangePattern]	=
				byteRangeSpec either suffixByteRangeSpec map {
					case Left((a, None))	=> Begin(a)
					case Left((a, Some(b)))	=> FromTo(a, b)
					case Right(b)			=> End(b)
				}
	}

	//------------------------------------------------------------------------------

	final case class Begin(start:Long)				extends RangePattern
	final case class FromTo(start:Long, end:Long)	extends RangePattern
	final case class End(size:Long)					extends RangePattern
}

sealed trait RangePattern {
	def toInclusiveRange(total:Long):Option[InclusiveRange]	= {
		val last	= total - 1
		this matchOption {
			case RangePattern.FromTo(start, end)	if start >= 0 && start <= last && end < last	=> InclusiveRange(start,			end)
			case RangePattern.Begin(start)			if start >= 0 && start <= last					=> InclusiveRange(start,			last)
			case RangePattern.End(count)			if count > 0  && count <= total					=> InclusiveRange(total - count,	last)
		}
	}

}
