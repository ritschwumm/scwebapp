package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

// TODO only handles basic authentication

// @see http://www.ietf.org/rfc/rfc2617.txt
object Authorization extends HeaderType[Authorization] {
	val key	= "Authorization"
	
	def parse(it:String):Option[Authorization]	=
			parsers.finished parseStringOption it
		
	def unparse(it:Authorization):String	=
			BasicAuthorization unparse it.value
		
	private object parsers {
		import HttpParsers._
		
		val value:CParser[Authorization]	= BasicAuthorization.parser map Authorization.apply
		val finished:CParser[Authorization]	= value finish LWSP
	}
}

final case class Authorization(value:BasicAuthorization)
