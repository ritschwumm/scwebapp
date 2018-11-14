package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object ContentRange extends HeaderType[ContentRange] {
	val key	= "Content-Range"

	def parse(it:String):Option[ContentRange]	=
			parsers.finished parseStringOption it

	def unparse(it:ContentRange):String	=
			ContentRangeValue unparse it.value

	private object parsers {
		import HttpParsers._

		val value:CParser[ContentRange]		= ContentRangeValue.parser map ContentRange.apply
		val finished:CParser[ContentRange]	= value finish LWSP
	}
}

final case class ContentRange(value:ContentRangeValue)
