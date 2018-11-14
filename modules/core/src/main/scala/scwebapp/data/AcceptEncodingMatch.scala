package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object AcceptEncodingMatch {
	val default	= AcceptEncodingMatch(AcceptEncodingFixed(AcceptEncodingIdentity), Some(QValue.one))

	//------------------------------------------------------------------------------

	lazy val parser:CParser[AcceptEncodingMatch]	= parsers.value

	def unparse(it:AcceptEncodingMatch):String	=
			(AcceptEncodingPattern	unparse		it.pattern) +
			(HttpUnparsers			qParamPart	it.quality)

	private object parsers {
		import HttpParsers._

		val value:CParser[AcceptEncodingMatch]		=
				AcceptEncodingPattern.parser next (symbol(';') right qParam).option map (AcceptEncodingMatch.apply _).tupled
	}
}

final case class AcceptEncodingMatch(
	pattern:AcceptEncodingPattern,
	quality:Option[QValue]
) {
	val totalQuality	= quality getOrElse QValue.one

	def acceptance(typ:AcceptEncodingType):Option[(Int,QValue)]	=
			pattern matches typ map { _ -> totalQuality }
}
