package scwebapp.data

import scutil.base.implicits._

import scwebapp.format._
import scparse.ng.text._

object ContentRangeValue {
	def total(size:Long):ContentRangeValue	=
			Total(size)

	def full(range:InclusiveRange, size:Long):ContentRangeValue	=
			Full(range, size)

	//------------------------------------------------------------------------------

	lazy val parser:TextParser[ContentRangeValue]	= parsers.value

	def unparse(it:ContentRangeValue):String	=
		it match {
			case Full(InclusiveRange(start, end), total)	=> show"${RangeType.keys.bytes} ${start}-${end}/${total}"
			case Bare(InclusiveRange(start, end))			=> show"${RangeType.keys.bytes} ${start}-${end}/*"
			case Total(total)								=> show"${RangeType.keys.bytes} */${total}"
		}

	private object parsers {
		import HttpParsers._

		val STAR	= TextParser is '*'
		val DASH	= TextParser is '-'
		val SLASH	= TextParser is '/'

		val irange:TextParser[InclusiveRange]	=
			longUnsigned left DASH next longUnsigned map { case (s, e) =>
				InclusiveRange(s, e)
			}

		val full:TextParser[ContentRangeValue]		= irange left SLASH next longUnsigned map Full.tupled
		val fromTo:TextParser[ContentRangeValue]		= irange left SLASH left STAR map Bare.apply
		val total:TextParser[ContentRangeValue]		= STAR right longUnsigned map Total.apply
		val rangeValue:TextParser[ContentRangeValue]	= full orElse fromTo orElse total

		val value:TextParser[ContentRangeValue]		= symbolN(RangeType.keys.bytes) right rangeValue
	}

	//------------------------------------------------------------------------------

	final case class Bare(irange:InclusiveRange)			extends ContentRangeValue
	final case class Total(size:Long)						extends ContentRangeValue
	final case class Full(irange:InclusiveRange, size:Long)	extends ContentRangeValue
}

sealed trait ContentRangeValue
