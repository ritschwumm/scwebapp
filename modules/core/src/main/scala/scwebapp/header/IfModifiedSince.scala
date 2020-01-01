package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scparse.ng.text._

object IfModifiedSince extends HeaderType[IfModifiedSince] {
	val key	= "If-Modified-Since"

	def parse(it:String):Option[IfModifiedSince]	=
			parsers.finished.parseString(it).toOption

	def unparse(it:IfModifiedSince):String	=
			HttpDate unparse it.value

	private object parsers {
		import HttpParsers._

		val value:TextParser[IfModifiedSince]		= dateValue map IfModifiedSince.apply
		val finished:TextParser[IfModifiedSince]	= value finish LWSP
	}
}

final case class IfModifiedSince(value:HttpDate) {
	// TODO why add a second here?
	def wasModified(modificationTime:HttpDate):Boolean	=
			value + HttpDuration.second > modificationTime
}
