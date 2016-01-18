package scwebapp.data

import scutil.implicits._

object ResponseRange {
	def total(size:Long):ResponseRange	=
			ResponseRangeTotal(size)
		
	def full(range:InclusiveRange, size:Long):ResponseRange	=
			ResponseRangeFull(range, size)
		
	def unparse(it:ResponseRange):String	=
			it match {
				case ResponseRangeFull(InclusiveRange(start, end), total)	=> so"bytes ${start.toString}-${end.toString}/${total.toString}"
				case ResponseRangeBare(InclusiveRange(start, end))			=> so"bytes ${start.toString}-${end.toString}/*"
				case ResponseRangeTotal(total)								=> so"bytes */${total.toString}"
			}
}

sealed trait ResponseRange

final case class ResponseRangeBare(irange:InclusiveRange)				extends ResponseRange
final case class ResponseRangeTotal(size:Long)							extends ResponseRange
final case class ResponseRangeFull(irange:InclusiveRange, size:Long)	extends ResponseRange
