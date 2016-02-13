package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object IfUnmodifiedSince extends HeaderType[IfUnmodifiedSince] {
	val key	= "If-Unmodified-Since"
	
	def parse(it:String):Option[IfUnmodifiedSince]	=
			parsers.finished parseStringOption it
		
	def unparse(it:IfUnmodifiedSince):String	=
			HttpDate unparse it.value
		
	private object parsers {
		import HttpParsers._
		
		val value:CParser[IfUnmodifiedSince]	= dateValue map IfUnmodifiedSince.apply
		val finished:CParser[IfUnmodifiedSince]	= value finish LWSP
	}
}

final case class IfUnmodifiedSince(value:HttpDate) {
	// TODO why add a second here?
	def wasModified(modificationTime:HttpDate):Boolean	=
			value + HttpDuration.second > modificationTime
}
