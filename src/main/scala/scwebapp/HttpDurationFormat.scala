package scwebapp

import scutil.lang._
import scutil.implicits._

object HttpDurationFormat {
	val prism:Prism[String,HttpDuration]	=
			Prism(parse, unparse)
	
	def unparse(duration:HttpDuration):String	=
			duration.seconds.toString

	def parse(str:String):Option[HttpDuration]	=
			str.toLongOption map HttpDuration.apply
}
