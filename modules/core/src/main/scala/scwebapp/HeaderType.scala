package scwebapp

import scutil.core.implicits.*

trait HeaderType[T] {
	implicit val HT:HeaderType[T]	= this

	def key:String
	def parse(it:String):Option[T]
	def unparse(it:T):String

	def parseEither(it:String):Either[String,T]	=
		parse(it).toRight(show"invalid ${key} header value ${it}")

	def headerValue(it:T):HeaderValue	=
		HeaderValue(key, unparse(it))
}
