package scwebapp.header

import scwebapp.HeaderType
import scwebapp.format._
import scparse.ng.text._

object ContentLength extends HeaderType[ContentLength] {
	val key	= "Content-Length"

	def parse(it:String):Option[ContentLength]	=
			parsers.finished.parseString(it).toOption

	def unparse(it:ContentLength):String	=
			it.value.toString

	private object parsers {
		import HttpParsers._

		val value:TextParser[ContentLength]		= longUnsigned eatLeft LWSP map ContentLength.apply
		val finished:TextParser[ContentLength]	= value finish LWSP
	}
}

final case class ContentLength(value:Long)
