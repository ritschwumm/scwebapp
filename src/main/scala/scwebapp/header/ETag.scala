package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object ETag extends HeaderType[ETag] {
	val key	= "ETag"
	
	def parse(it:String):Option[ETag]	=
			parsers.finished parseStringOption it
		
	def unparse(it:ETag):String	=
			ETagValue unparse it.value
		
	private object parsers {
		import HttpParsers._
		
		val value:CParser[ETag]		= ETagValue.parser map ETag.apply
		val finished:CParser[ETag]	= value finish LWSP
	}
}

final case class ETag(value:ETagValue)
