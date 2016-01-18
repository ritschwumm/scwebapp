package scwebapp

object HttpRange {
	def full(total:Long):HttpRange	=
			HttpRange(0, total - 1, total)
}

final case class HttpRange(start:Long, end:Long, total:Long) {
	// start and end are both inclusive
	val length	= end - start + 1
	
	def toHttpOutRange:HttpOutRange	=
			HttpOutRangeFull(start, end, total)
}
