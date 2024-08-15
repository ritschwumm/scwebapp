package scwebapp.data

import scwebapp.format.*
import scparse.ng.text.*

object MediaRange {
	lazy val parser:TextParser[MediaRange]	= parsers.value

	def unparse(it:MediaRange):String	=
		MediaValue.unparse(it.value)	+
		HttpUnparsers.parameterList(it.parameters)

	private object parsers {
		import HttpParsers.*

		val value:TextParser[MediaRange]	=
			MediaValue.parser.next(parameterList).map(MediaRange.apply.tupled)
	}
}

final case class MediaRange(value:MediaValue, parameters:NoCaseParameters) {
	// TODO include parameters in the match of the pattern?
	def acceptance(typ:MimeType):Option[(Int,QValue)]	=
		value.acceptance(typ)
}
