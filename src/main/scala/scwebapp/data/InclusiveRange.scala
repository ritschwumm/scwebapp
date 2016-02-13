package scwebapp.data

object InclusiveRange {
	def full(total:Long):InclusiveRange	=
			InclusiveRange(0, total - 1)
}

/** start and end are both inclusive */
final case class InclusiveRange(start:Long, end:Long) {
	val length	= end - start + 1
}
