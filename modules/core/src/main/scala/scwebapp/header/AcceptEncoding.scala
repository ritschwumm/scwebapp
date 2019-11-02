package scwebapp.header

import scutil.base.implicits._
import scutil.lang._

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._
import scwebapp.util.AcceptanceUtil

object AcceptEncoding extends HeaderType[AcceptEncoding] {
	val key	= "Accept-Encoding"

	def parse(it:String):Option[AcceptEncoding]	=
			parsers.finished parseStringOption it

	def unparse(it:AcceptEncoding):String	=
			it.matches map AcceptEncodingMatch.unparse mkString ","

	object parsers {
		import HttpParsers._

		val value:CParser[AcceptEncoding]		= hash(AcceptEncodingMatch.parser) map AcceptEncoding.apply
		val finished:CParser[AcceptEncoding]	= value finish LWSP
	}
}

final case class AcceptEncoding(matches:ISeq[AcceptEncodingMatch]) {
	def accepts(typ:AcceptEncodingType):Boolean	=
			acceptance(typ) > QValue.zero

	def acceptance(typ:AcceptEncodingType):QValue	=
			(AcceptanceUtil acceptance matches)(_ acceptance typ) getOrElse (
				// NOTE this is not duplicate...
				typ == AcceptEncodingType.Identity cata (QValue.zero, QValue.one)
			)
}
