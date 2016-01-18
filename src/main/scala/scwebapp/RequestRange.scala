package scwebapp

object RequestRange {
	def full(total:Long):RequestRange	=
			RequestRange(InclusiveRange full total, total)
}

final case class RequestRange(range:InclusiveRange, total:Long) {
	val length	= range.length
	
	def toResponseRange:ResponseRange	=
			ResponseRange full (range, total)
}
