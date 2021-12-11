package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object ContentType extends HeaderType[ContentType] {
	val key	= "Content-Type"

	def parse(it:String):Option[ContentType]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:ContentType):String	=
		MimeType unparse it.typ

	private object parsers {
		import HttpParsers.*

		val value:TextParser[ContentType]		= MimeType.parser map ContentType.apply
		val finished:TextParser[ContentType]	= value finishRight LWSP
	}
}

final case class ContentType(typ:MimeType)
