package scwebapp.data

import scwebapp.format.*
import scparse.ng.text.*

object RangeType {
	// TODO get rid of this
	object keys {
		val bytes	= "bytes"
		val none	= "none"
	}

	lazy val parser:TextParser[RangeType]	= parsers.value

	def unparse(it:RangeType):String	=
		it match {
			case IsNone	=> "none"
			case Bytes	=> "bytes"
		}

	private object parsers {
		import HttpParsers.*

		val value:TextParser[RangeType]		=
			token
			.collect[RangeType] {
				case "none"		=> IsNone
				case "bytes"	=> Bytes
			}
			.named("RangeType")
	}
}

enum RangeType {
	case IsNone
	case Bytes
}
