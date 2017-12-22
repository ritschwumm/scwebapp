package scwebapp

object HeaderValue {
	implicit def fromHeader[T:HeaderType](header:T):HeaderValue	=
			implicitly[HeaderType[T]] headerValue header
}

final case class HeaderValue(name:String, value:String)
