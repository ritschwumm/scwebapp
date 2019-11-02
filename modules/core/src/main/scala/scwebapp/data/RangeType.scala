package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object RangeType {
	// TODO get rid of this
	object keys {
		val bytes	= "bytes"
		val none	= "none"
	}

	lazy val parser:CParser[RangeType]	= parsers.value

	def unparse(it:RangeType):String	=
			it match {
				case IsNone	=> "none"
				case Bytes	=> "bytes"
			}

	private object parsers {
		import HttpParsers._

		val value:CParser[RangeType]		=
				token collect {
					case "none"		=> IsNone
					case "bytes"	=> Bytes
				}
	}

	case object IsNone	extends RangeType
	case object Bytes	extends RangeType
}

sealed trait RangeType
