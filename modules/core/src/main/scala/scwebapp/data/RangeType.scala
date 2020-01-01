package scwebapp.data

import scwebapp.format._
import scparse.ng.text._

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
		import HttpParsers._

		val value:TextParser[RangeType]		=
				token
				.requirePartial[RangeType] {
					case "none"		=> IsNone
					case "bytes"	=> Bytes
				}
				.named("RangeType")
	}

	case object IsNone	extends RangeType
	case object Bytes	extends RangeType
}

sealed trait RangeType
