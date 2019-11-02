package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object ContentDispositionType {
	lazy val parser:CParser[ContentDispositionType]	= parsers.value

	def unparse(it:ContentDispositionType):String	=
			it match {
				case Attachment	=> "attachment"
				case Inline		=> "inline"
			}

	private object parsers {
		import HttpParsers._

		val value:CParser[ContentDispositionType]	=
				token map CaseUtil.lowerCase collect {
					case "attachment"	=> Attachment
					case "inline"		=> Inline
				}
	}

	//------------------------------------------------------------------------------

	case object Attachment	extends ContentDispositionType
	case object Inline		extends ContentDispositionType
}

sealed trait ContentDispositionType
