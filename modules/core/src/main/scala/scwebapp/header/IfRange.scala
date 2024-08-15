package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object IfRange extends HeaderType[IfRange] {
	val key	= "If-Range"

	def parse(it:String):Option[IfRange]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:IfRange):String	=
		IfRangeValue.unparse(it.value)

	private object parsers {
		import HttpParsers.*

		val value:TextParser[IfRange]		= IfRangeValue.parser.map(IfRange.apply)
		val finished:TextParser[IfRange]	= value.finishRight(LWSP)
	}
}

final case class IfRange(value:IfRangeValue) {
	def needsFull(eTag:ETagValue, lastModified:HttpDate):Boolean	=
		value.needsFull(eTag, lastModified)
}
