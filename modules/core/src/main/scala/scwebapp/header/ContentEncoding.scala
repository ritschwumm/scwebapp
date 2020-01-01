package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scparse.ng.text._

object ContentEncoding extends HeaderType[ContentEncoding] {
	val key	= "Content-Encoding"

	def parse(it:String):Option[ContentEncoding]	=
			parsers.finished.parseString(it).toOption

	def unparse(it:ContentEncoding):String	=
			ContentEncodingType unparse it.typ

	private object parsers {
		import HttpParsers._

		val value:TextParser[ContentEncoding]		= ContentEncodingType.parser map ContentEncoding.apply
		val finished:TextParser[ContentEncoding]	= value finish LWSP
	}
}

final case class ContentEncoding(typ:ContentEncodingType)
