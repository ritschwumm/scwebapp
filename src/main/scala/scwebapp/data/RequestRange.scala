package scwebapp.data

import scutil.implicits._

object RequestRange {
	def full(total:Long):RequestRange	=
			RequestRange(InclusiveRange full total, total)
		
	def parse(total:Long)(it:RangeValue):Option[RequestRange]	= {
		val last	= total - 1
		it matchOption {
			case RangeFromTo(start, end)	if start >= 0 && start <= last && end < last	=> RequestRange(InclusiveRange(start,			end),	total)
			case RangeBegin(start)			if start >= 0 && start <= last					=> RequestRange(InclusiveRange(start,			last),	total)
			case RangeEnd(count)			if count > 0  && count <= total					=> RequestRange(InclusiveRange(total - count,	last),	total)
		}
	}
}

final case class RequestRange(range:InclusiveRange, total:Long) {
	val length	= range.length
	
	def toResponseRange:ResponseRange	=
			ResponseRange full (range, total)
}
