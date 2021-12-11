package scwebapp

import scala.language.implicitConversions

object HeaderValue {
	implicit def fromHeader[T:HeaderType](header:T):HeaderValue	=
		summon[HeaderType[T]] headerValue header
}

final case class HeaderValue(name:String, value:String)
