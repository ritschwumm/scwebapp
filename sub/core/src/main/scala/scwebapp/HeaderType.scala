package scwebapp

import scutil.base.implicits._
import scutil.lang._

trait HeaderType[T] {
	implicit val HT:HeaderType[T]	= this
	
	def key:String	
	def parse(it:String):Option[T]
	def unparse(it:T):String
	
	def parseTried(it:String):Tried[String,T]	=
			parse(it) toWin so"invalid ${key} header value ${it}"
	
	def headerValue(it:T):HeaderValue	=
			HeaderValue(key, unparse(it))
}
