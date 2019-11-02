package scwebapp.data

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.text.ParseException
import java.util.Date
import java.util.Locale
import java.util.TimeZone

import scutil.base.implicits._
import scutil.lang._
import scutil.time.MilliInstant

object HttpDate {
	val zero:HttpDate	= HttpDate(0)
	def now():HttpDate	= HttpDate(System.currentTimeMillis / 1000)

	def fromDate(it:Date):HttpDate					= HttpDate(it.getTime / 1000)
	def fromMilliInstant(it:MilliInstant):HttpDate	= HttpDate(it.millis / 1000)

	def toDate(it:HttpDate):Date					= new Date(it.seconds * 1000)
	def toMilliInstant(it:HttpDate):MilliInstant	= MilliInstant(it.seconds * 1000)

	//------------------------------------------------------------------------------

	def parse(str:String):Option[HttpDate]	=
			synchronized {
				allFormats collapseMapFirst parseDateWith(str)
			}

	def unparse(date:HttpDate):String	=
			synchronized {
				standardFormat format date.toDate
			}

	//------------------------------------------------------------------------------

	private val gmtZone:TimeZone	=
			TimeZone getTimeZone "GMT"

	private def mkFormat(pattern:String):DateFormat	=
			new SimpleDateFormat(pattern, Locale.US) doto { _ setTimeZone gmtZone }

	private val standardFormat:DateFormat =
			// RFC 822, updated by RFC 1123
			mkFormat("EEE, dd MMM yyyy HH:mm:ss zzz")

	private val allFormats:ISeq[DateFormat]	=
			Vector(
				standardFormat,
				// RFC 850, obsoleted by RFC 1036
				mkFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz"),
				// ANSI C's asctime() format
				mkFormat("EEE MMMM d HH:mm:ss yyyy")
			)

	private def parseDateWith(str:String)(format:DateFormat):Option[HttpDate]	=
			try {
				Some(HttpDate fromDate (format parse str))
			}
			catch { case e:ParseException =>
				None
			}
}

final case class HttpDate(seconds:Long) extends Ordered[HttpDate] {
	def +(offset:HttpDuration):HttpDate	= copy(seconds = seconds + offset.seconds)
	def -(offset:HttpDuration):HttpDate	= copy(seconds = seconds - offset.seconds)

	def compare(that:HttpDate):Int	= this.seconds compare that.seconds

	def toDate:Date					= HttpDate toDate			this
	def toMilliInstant:MilliInstant	= HttpDate toMilliInstant	this
}
