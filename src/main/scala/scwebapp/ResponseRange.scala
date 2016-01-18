package scwebapp

import scutil.implicits._

object ResponseRange {
	def total(size:Long):ResponseRange	=
			ResponseRange(Two(size))
		
	def full(range:InclusiveRange, size:Long):ResponseRange	=
			ResponseRange(OneAndTwo(range, size))
		
	def unparse(it:ResponseRange):String	=
			it.range match {
				case OneAndTwo(InclusiveRange(start, end), total)	=> so"bytes ${start.toString}-${end.toString}/${total.toString}"
				case One(InclusiveRange(start, end))				=> so"bytes ${start.toString}-${end.toString}/*"
				case Two(total)										=> so"bytes */${total.toString}"
			}
}

final case class ResponseRange(range:OneOrTwo[InclusiveRange,Long])
