package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scparse.ng.text._

object ETag extends HeaderType[ETag] {
	val key	= "ETag"

	def parse(it:String):Option[ETag]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:ETag):String	=
		ETagValue unparse it.value

	private object parsers {
		import HttpParsers._

		val value:TextParser[ETag]		= ETagValue.parser map ETag.apply
		val finished:TextParser[ETag]	= value finishRight LWSP
	}
}

final case class ETag(value:ETagValue)
