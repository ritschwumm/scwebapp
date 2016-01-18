package scwebapp.data

import scutil.implicits._

object RangeType {
	def parse(it:String):Option[RangeType]	=
			it matchOption {
				case "none"		=> RangeTypeNone
				case "bytes"	=> RangeTypeBytes
			}
			
	def unparse(it:RangeType):String	=
			it match {
				case RangeTypeNone	=> "none"
				case RangeTypeBytes	=> "bytes"
			}
}

sealed trait RangeType
case object RangeTypeNone	extends RangeType
case object RangeTypeBytes	extends RangeType
