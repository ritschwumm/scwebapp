package scwebapp.data

sealed trait RangeValue
final case class RangeBegin(start:Long)				extends RangeValue
final case class RangeFromTo(start:Long, end:Long)	extends RangeValue
final case class RangeEnd(size:Long)				extends RangeValue
