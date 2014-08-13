package scwebapp

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.text.ParseException
import java.util.Locale
import java.util.TimeZone

import scutil.lang._
import scutil.implicits._
import scutil.time.MilliInstant

object HttpDateFormat {
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
			
	//------------------------------------------------------------------------------
	
	val prism:Prism[String,HttpDate]	=
			Prism(parse, unparse)
	
	def unparse(date:HttpDate):String	=
			synchronized {
				standardFormat format date.toDate
			}

	def parse(str:String):Option[HttpDate]	=
			synchronized {
				allFormats collapseFirst parseDateWith(str)
			}
			
	//------------------------------------------------------------------------------
	
	private def parseDateWith(str:String)(format:DateFormat):Option[HttpDate]	=
			try {
				Some(HttpDate fromDate (format parse str))
			}
			catch { case e:ParseException =>
				None
			}
}
