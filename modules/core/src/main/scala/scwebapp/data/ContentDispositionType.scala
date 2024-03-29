package scwebapp.data

import scwebapp.format.*
import scparse.ng.text.*

object ContentDispositionType {
	lazy val parser:TextParser[ContentDispositionType]	= parsers.value

	def unparse(it:ContentDispositionType):String	=
		it match {
			case Attachment	=> "attachment"
			case Inline		=> "inline"
		}

	private object parsers {
		import HttpParsers.*

		val value:TextParser[ContentDispositionType]	=
			token
			.map (CaseUtil.lowerCase)
			.collect[ContentDispositionType] {
				case "attachment"	=> Attachment
				case "inline"		=> Inline
			}
			.named ("ContentDispositionType")
	}
}

enum ContentDispositionType {
	case Attachment
	case Inline
}
