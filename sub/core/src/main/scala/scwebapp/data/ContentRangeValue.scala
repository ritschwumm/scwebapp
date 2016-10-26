package scwebapp.data

import scutil.base.implicits._

import scwebapp.format._
import scwebapp.parser.string._

object ContentRangeValue {
	def total(size:Long):ContentRangeValue	=
			ContentRangeTotal(size)
		
	def full(range:InclusiveRange, size:Long):ContentRangeValue	=
			ContentRangeFull(range, size)
		
	//------------------------------------------------------------------------------
		
	lazy val parser:CParser[ContentRangeValue]	= parsers.value
		
	def unparse(it:ContentRangeValue):String	=
			it match {
				case ContentRangeFull(InclusiveRange(start, end), total)	=> so"${RangeType.keys.bytes} ${start.toString}-${end.toString}/${total.toString}"
				case ContentRangeBare(InclusiveRange(start, end))			=> so"${RangeType.keys.bytes} ${start.toString}-${end.toString}/*"
				case ContentRangeTotal(total)								=> so"${RangeType.keys.bytes} */${total.toString}"
			}
				
	private object parsers {
		import HttpParsers._
		
		val STAR	= cis('*')
		val DASH	= cis('-')
		val SLASH	= cis('/')
		
		val irange:CParser[InclusiveRange]	=
				longUnsigned left DASH next longUnsigned map { case (s, e) =>
					InclusiveRange(s, e)
				}
		
		val full:CParser[ContentRangeValue]			= irange left SLASH next longUnsigned map ContentRangeFull.tupled
		val fromTo:CParser[ContentRangeValue]		= irange left SLASH left STAR map ContentRangeBare.apply
		val total:CParser[ContentRangeValue]		= STAR right longUnsigned map ContentRangeTotal.apply
		val rangeValue:CParser[ContentRangeValue]	= full orElse fromTo orElse total
		
		val value:CParser[ContentRangeValue]		= symbolN(RangeType.keys.bytes) right rangeValue
	}
}

sealed trait ContentRangeValue
final case class ContentRangeBare(irange:InclusiveRange)			extends ContentRangeValue
final case class ContentRangeTotal(size:Long)						extends ContentRangeValue
final case class ContentRangeFull(irange:InclusiveRange, size:Long)	extends ContentRangeValue
