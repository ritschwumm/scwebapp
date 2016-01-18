package scwebapp.data

object SimpleRange {
	def full(total:Long):SimpleRange	=
			SimpleRange(0, total - 1)
}
/** start and end are both inclusive */
final case class SimpleRange(start:Long, end:Long) {
	val length	= end - start + 1
}
