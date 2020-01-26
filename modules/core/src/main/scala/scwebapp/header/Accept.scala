package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scparse.ng.text._
import scwebapp.util.AcceptanceUtil

object Accept extends HeaderType[Accept] {
	val key	= "Accept"

	def parse(it:String):Option[Accept]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:Accept):String	=
		it.ranges map MediaRange.unparse mkString ","

	private object parsers {
		import HttpParsers._

		val value:TextParser[Accept]	= hash(MediaRange.parser) map Accept.apply
		val finished:TextParser[Accept]	= value finishRight LWSP
	}
}

final case class Accept(ranges:Seq[MediaRange]) {
	def accepts(typ:MimeType):Boolean	=
		acceptance(typ) > QValue.zero

	def acceptance(typ:MimeType):QValue	=
		(AcceptanceUtil acceptance ranges)(_ acceptance typ) getOrElse QValue.one
}
