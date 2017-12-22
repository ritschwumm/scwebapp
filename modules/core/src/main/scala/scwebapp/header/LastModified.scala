package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object LastModified extends HeaderType[LastModified] {
	val key	= "Last-Modified"
	
	def parse(it:String):Option[LastModified]	=
			parsers.finished parseStringOption it
		
	def unparse(it:LastModified):String	=
			HttpDate unparse it.value
		
	private object parsers {
		import HttpParsers._
		
		val value:CParser[LastModified]		= dateValue map LastModified.apply
		val finished:CParser[LastModified]	= value finish LWSP
	}
}

final case class LastModified(value:HttpDate)
