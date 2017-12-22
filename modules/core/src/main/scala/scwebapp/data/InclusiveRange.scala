package scwebapp.data

object InclusiveRange {
	def full(total:Long):InclusiveRange	=
			InclusiveRange(0, total - 1)
		
	def startLength(start:Long, length:Long):InclusiveRange	=
			InclusiveRange(start, start + length - 1)
}

/** start and end are both inclusive */
final case class InclusiveRange(start:Long, end:Long) {
	val length	= end - start + 1
}
