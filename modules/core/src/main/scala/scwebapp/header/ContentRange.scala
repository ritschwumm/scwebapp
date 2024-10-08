package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object ContentRange extends HeaderType[ContentRange] {
	val key	= "Content-Range"

	def parse(it:String):Option[ContentRange]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:ContentRange):String	=
		ContentRangeValue.unparse(it.value)

	private object parsers {
		import HttpParsers.*

		val value:TextParser[ContentRange]		= ContentRangeValue.parser.map(ContentRange.apply)
		val finished:TextParser[ContentRange]	= value.finishRight(LWSP)
	}
}

final case class ContentRange(value:ContentRangeValue)
