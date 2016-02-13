package scwebapp.header

import scutil.lang._

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._
import scwebapp.util.AcceptanceUtil

object Accept extends HeaderType[Accept] {
	val key	= "Accept"
	
	def parse(it:String):Option[Accept]	=
			parsers.finished parseStringOption it
		
	def unparse(it:Accept):String	=
			it.ranges map MediaRange.unparse mkString ","
		
	private object parsers {
		import HttpParsers._
			
		val value:CParser[Accept]		= hash(MediaRange.parser) map Accept.apply
		val finished:CParser[Accept]	= value finish LWSP
	}
}

final case class Accept(ranges:ISeq[MediaRange]) {
	def accepts(typ:MimeType):Boolean	=
			acceptance(typ) > QValue.zero
		
	def acceptance(typ:MimeType):QValue	=
			(AcceptanceUtil acceptance ranges)(_ acceptance typ) getOrElse QValue.one
}
