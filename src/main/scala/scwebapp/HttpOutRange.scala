package scwebapp

import scutil.implicits._

object HttpOutRange {
	def unparse(it:HttpOutRange):String	=
			it match {
				case HttpOutRangeFull(start, end, total)	=> so"bytes ${start.toString}-${end.toString}/${total.toString}"
				case HttpOutRangeExtent(start, end)			=> so"bytes ${start.toString}-${end.toString}/*"
				case HttpOutRangeTotal(total)				=> so"bytes */${total.toString}"
			}
}

sealed trait HttpOutRange
case class HttpOutRangeFull(start:Long, end:Long, total:Long)	extends HttpOutRange
case class HttpOutRangeExtent(start:Long, end:Long)				extends HttpOutRange
case class HttpOutRangeTotal(total:Long)						extends HttpOutRange
