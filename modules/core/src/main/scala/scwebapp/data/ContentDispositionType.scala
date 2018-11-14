package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object ContentDispositionType {
	lazy val parser:CParser[ContentDispositionType]	= parsers.value

	def unparse(it:ContentDispositionType):String	=
			it match {
				case ContentDispositionAttachment	=> "attachment"
				case ContentDispositionInline		=> "inline"
			}

	private object parsers {
		import HttpParsers._

		val value:CParser[ContentDispositionType]	=
				token map CaseUtil.lowerCase collect {
					case "attachment"	=> ContentDispositionAttachment
					case "inline"		=> ContentDispositionInline
				}
	}
}

sealed trait ContentDispositionType
case object ContentDispositionAttachment	extends ContentDispositionType
case object ContentDispositionInline		extends ContentDispositionType
