package scwebapp.header

import scwebapp.HeaderType
import scwebapp.format._
import scwebapp.parser.string._

object ContentLength extends HeaderType[ContentLength] {
	val key	= "Content-Length"
	
	def parse(it:String):Option[ContentLength]	=
			parsers.finished parseStringOption it
		
	def unparse(it:ContentLength):String	=
			it.value.toString
		
	private object parsers {
		import HttpParsers._
		
		val value:CParser[ContentLength]	= longUnsigned eating LWSP map ContentLength.apply
		val finished:CParser[ContentLength]	= value finish LWSP
	}
}

final case class ContentLength(value:Long)
