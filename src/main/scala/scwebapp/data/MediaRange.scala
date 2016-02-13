package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object MediaRange {
	lazy val parser:CParser[MediaRange]	= parsers.value
		
	def unparse(it:MediaRange):String	=
			(MediaValue unparse it.value)	+
			(HttpUnparsers parameterList it.parameters)
			
	private object parsers {
		import HttpParsers._
		
		val value:CParser[MediaRange]	=
				MediaValue.parser next parameterList map (MediaRange.apply _).tupled
	}
}

case class MediaRange(value:MediaValue, parameters:NoCaseParameters) {
	// TODO include parameters in the match of the pattern?
	def acceptance(typ:MimeType):Option[(Int,QValue)]	=
			value acceptance typ
}
