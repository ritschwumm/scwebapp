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
				case RangeTypeNone	=> "none"
				case RangeTypeBytes	=> "bytes"
			}
			
	private object parsers {
		import HttpParsers._
		
		val value:CParser[RangeType]		=
				token collect {
					case "none"		=> RangeTypeNone
					case "bytes"	=> RangeTypeBytes
				}
	}
}

sealed trait RangeType
case object RangeTypeNone	extends RangeType
case object RangeTypeBytes	extends RangeType
