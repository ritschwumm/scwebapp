package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object ContentType extends HeaderType[ContentType] {
	val key	= "Content-Type"
	
	def parse(it:String):Option[ContentType]	=
			parsers.finished parseStringOption it
		
	def unparse(it:ContentType):String	=
			MimeType unparse it.typ
		
	private object parsers {
		import HttpParsers._
		
		val value:CParser[ContentType]		= MimeType.parser map ContentType.apply
		val finished:CParser[ContentType]	= value finish LWSP
	}
}

final case class ContentType(typ:MimeType)
