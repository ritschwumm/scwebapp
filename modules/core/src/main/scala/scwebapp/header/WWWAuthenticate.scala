package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object WWWAuthenticate extends HeaderType[WWWAuthenticate] {
	val key	= "WWW-Authenticate"
	
	def parse(it:String):Option[WWWAuthenticate]	=
			parsers.finished parseStringOption it
	
	def unparse(it:WWWAuthenticate):String	=
			BasicAuthenticate unparse it.value
		
	private object parsers {
		import HttpParsers._
		
		val value:CParser[WWWAuthenticate]		= BasicAuthenticate.parser map WWWAuthenticate.apply
		val finished:CParser[WWWAuthenticate]	= value finish LWSP
	}
}

final case class WWWAuthenticate(value:BasicAuthenticate)
