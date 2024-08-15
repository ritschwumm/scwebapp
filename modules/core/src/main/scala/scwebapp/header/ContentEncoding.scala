package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object ContentEncoding extends HeaderType[ContentEncoding] {
	val key	= "Content-Encoding"

	def parse(it:String):Option[ContentEncoding]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:ContentEncoding):String	=
		ContentEncodingType.unparse(it.typ)

	private object parsers {
		import HttpParsers.*

		val value:TextParser[ContentEncoding]		= ContentEncodingType.parser.map(ContentEncoding.apply)
		val finished:TextParser[ContentEncoding]	= value.finishRight(LWSP)
	}
}

final case class ContentEncoding(typ:ContentEncodingType)
