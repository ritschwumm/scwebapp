package scwebapp.data

import scwebapp.format.*
import scparse.ng.text.*

object AcceptEncodingMatch {
	val default	= AcceptEncodingMatch(AcceptEncodingPattern.Fixed(AcceptEncodingType.Identity), Some(QValue.one))

	//------------------------------------------------------------------------------

	lazy val parser:TextParser[AcceptEncodingMatch]	= parsers.value

	def unparse(it:AcceptEncodingMatch):String	=
		AcceptEncodingPattern.unparse(it.pattern) +
		HttpUnparsers.qParamPart(it.quality)

	private object parsers {
		import HttpParsers.*

		val qParam:TextParser[QValue]	=
			symbol('q').right(symbol('=')).right((QValue.parser.eatLeft(LWSP)))

		val value:TextParser[AcceptEncodingMatch]		=
			AcceptEncodingPattern.parser.next(symbol(';').right(qParam).option).map(AcceptEncodingMatch.apply.tupled)
	}
}

final case class AcceptEncodingMatch(
	pattern:AcceptEncodingPattern,
	quality:Option[QValue]
) {
	val totalQuality	= quality.getOrElse(QValue.one)

	def acceptance(typ:AcceptEncodingType):Option[(Int,QValue)]	=
		pattern.matches(typ).map(_ -> totalQuality)
}
