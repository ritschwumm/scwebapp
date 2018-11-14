package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object ContentEncoding extends HeaderType[ContentEncoding] {
	val key	= "Content-Encoding"

	def parse(it:String):Option[ContentEncoding]	=
			parsers.finished parseStringOption it

	def unparse(it:ContentEncoding):String	=
			ContentEncodingType unparse it.typ

	private object parsers {
		import HttpParsers._

		val value:CParser[ContentEncoding]		= ContentEncodingType.parser map ContentEncoding.apply
		val finished:CParser[ContentEncoding]	= value finish LWSP
	}
}

final case class ContentEncoding(typ:ContentEncodingType)
