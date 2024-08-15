package scwebapp.header

import scutil.core.implicits.*

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*
import scwebapp.util.AcceptanceUtil

object AcceptEncoding extends HeaderType[AcceptEncoding] {
	val key	= "Accept-Encoding"

	def parse(it:String):Option[AcceptEncoding]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:AcceptEncoding):String	=
		it.matches map AcceptEncodingMatch.unparse mkString ","

	object parsers {
		import HttpParsers.*

		val value:TextParser[AcceptEncoding]	= hash(AcceptEncodingMatch.parser).map(AcceptEncoding.apply)
		val finished:TextParser[AcceptEncoding]	= value.finishRight(LWSP)
	}
}

final case class AcceptEncoding(matches:Seq[AcceptEncodingMatch]) {
	def accepts(typ:AcceptEncodingType):Boolean	=
		acceptance(typ) > QValue.zero

	def acceptance(typ:AcceptEncodingType):QValue	=
		AcceptanceUtil.acceptance(matches)(_.acceptance(typ)).getOrElse(
			// NOTE this is not duplicate...
			(typ == AcceptEncodingType.Identity).cata(QValue.zero, QValue.one)
		)
}
