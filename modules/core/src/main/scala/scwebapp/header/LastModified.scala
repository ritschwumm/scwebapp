package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object LastModified extends HeaderType[LastModified] {
	val key	= "Last-Modified"

	def parse(it:String):Option[LastModified]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:LastModified):String	=
		HttpDate.unparse(it.value)

	private object parsers {
		import HttpParsers.*

		val value:TextParser[LastModified]		= dateValue.map(LastModified.apply)
		val finished:TextParser[LastModified]	= value.finishRight(LWSP)
	}
}

final case class LastModified(value:HttpDate)
