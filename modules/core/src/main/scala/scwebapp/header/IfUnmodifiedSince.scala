package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object IfUnmodifiedSince extends HeaderType[IfUnmodifiedSince] {
	val key	= "If-Unmodified-Since"

	def parse(it:String):Option[IfUnmodifiedSince]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:IfUnmodifiedSince):String	=
		HttpDate.unparse(it.value)

	private object parsers {
		import HttpParsers.*

		val value:TextParser[IfUnmodifiedSince]		= dateValue.map(IfUnmodifiedSince.apply)
		val finished:TextParser[IfUnmodifiedSince]	= value.finishRight(LWSP)
	}
}

final case class IfUnmodifiedSince(value:HttpDate) {
	// TODO why add a second here?
	def wasModified(modificationTime:HttpDate):Boolean	=
		value + HttpDuration.second > modificationTime
}
