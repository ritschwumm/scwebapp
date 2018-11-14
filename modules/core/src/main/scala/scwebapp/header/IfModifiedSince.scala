package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object IfModifiedSince extends HeaderType[IfModifiedSince] {
	val key	= "If-Modified-Since"

	def parse(it:String):Option[IfModifiedSince]	=
			parsers.finished parseStringOption it

	def unparse(it:IfModifiedSince):String	=
			HttpDate unparse it.value

	private object parsers {
		import HttpParsers._

		val value:CParser[IfModifiedSince]		= dateValue map IfModifiedSince.apply
		val finished:CParser[IfModifiedSince]	= value finish LWSP
	}
}

final case class IfModifiedSince(value:HttpDate) {
	// TODO why add a second here?
	def wasModified(modificationTime:HttpDate):Boolean	=
			value + HttpDuration.second > modificationTime
}
