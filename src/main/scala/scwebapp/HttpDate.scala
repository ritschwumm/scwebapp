package scwebapp

import java.util.Date

import scutil.time.MilliInstant

object HttpDate {
	val zero:HttpDate	= HttpDate(0)
	def now:HttpDate	= HttpDate(System.currentTimeMillis / 1000)
	
	def fromDate(it:Date):HttpDate					= HttpDate(it.getTime / 1000)
	def fromMilliInstant(it:MilliInstant):HttpDate	= HttpDate(it.millis / 1000)
	
	def toDate(it:HttpDate):Date					= new Date(it.seconds * 1000)
	def toMilliInstant(it:HttpDate):MilliInstant	= MilliInstant(it.seconds * 1000)
}

final case class HttpDate(seconds:Long) extends Ordered[HttpDate] {
	def +(offset:Long):HttpDate	= copy(seconds = seconds + offset)
	def -(offset:Long):HttpDate	= copy(seconds = seconds - offset)
	
	def compare(that:HttpDate):Int	= this.seconds compare that.seconds
	
	def toDate:Date					= HttpDate toDate			this
	def toMilliInstant:MilliInstant	= HttpDate toMilliInstant	this
}
