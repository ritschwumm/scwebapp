package scwebapp.data

import scutil.time.MilliDuration

object HttpDuration {
	val zero:HttpDuration	= HttpDuration(0)

	val second:HttpDuration	= HttpDuration(1)
	val minute:HttpDuration	= second * 60
	val hour:HttpDuration	= minute * 60
	val day:HttpDuration	= hour * 24
	val week:HttpDuration	= day * 7

	def fromMilliDuration(it:MilliDuration):HttpDuration	= HttpDuration(it.millis  / 1000)
	def toMilliDuration(it:HttpDuration):MilliDuration		= MilliDuration(it.seconds * 1000)

	//------------------------------------------------------------------------------

	def unparse(duration:HttpDuration):String	=
		duration.seconds.toString

	def parse(str:String):Option[HttpDuration]	=
		str.toLongOption.map(HttpDuration.apply)
}

final case class HttpDuration(seconds:Long) extends Ordered[HttpDuration] {
	def *(factor:Long)	= copy(seconds = this.seconds * factor)
	def /(factor:Long)	= copy(seconds = this.seconds / factor)

	def +(that:HttpDuration):HttpDuration	= copy(seconds = this.seconds + that.seconds)
	def -(that:HttpDuration):HttpDuration	= copy(seconds = this.seconds - that.seconds)

	def compare(that:HttpDuration):Int	= this.seconds compare that.seconds

	def toMilliDuration:MilliDuration	= HttpDuration.toMilliDuration(this)
}
