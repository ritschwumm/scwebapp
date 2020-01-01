package scwebapp.data

import scwebapp.format._
import scparse.ng.text._

object ContentDispositionType {
	lazy val parser:TextParser[ContentDispositionType]	= parsers.value

	def unparse(it:ContentDispositionType):String	=
		it match {
			case Attachment	=> "attachment"
			case Inline		=> "inline"
		}

	private object parsers {
		import HttpParsers._

		val value:TextParser[ContentDispositionType]	=
			token
			.map (CaseUtil.lowerCase)
			.requirePartial[ContentDispositionType] {
				case "attachment"	=> Attachment
				case "inline"		=> Inline
			}
			.named ("ContentDispositionType")
	}

	//------------------------------------------------------------------------------

	case object Attachment	extends ContentDispositionType
	case object Inline		extends ContentDispositionType
}

sealed trait ContentDispositionType
