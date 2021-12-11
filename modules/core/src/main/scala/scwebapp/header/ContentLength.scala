package scwebapp.header

import scwebapp.HeaderType
import scwebapp.format.*
import scparse.ng.text.*

object ContentLength extends HeaderType[ContentLength] {
	val key	= "Content-Length"

	def parse(it:String):Option[ContentLength]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:ContentLength):String	=
		it.value.toString

	private object parsers {
		import HttpParsers.*

		val value:TextParser[ContentLength]		= longUnsigned eatLeft LWSP map ContentLength.apply
		val finished:TextParser[ContentLength]	= value finishRight LWSP
	}
}

final case class ContentLength(value:Long)
