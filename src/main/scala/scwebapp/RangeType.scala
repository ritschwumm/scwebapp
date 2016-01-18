package scwebapp

import scutil.implicits._

object RangeType {
	def parse(it:String):Option[RangeType]	=
			it matchOption {
				case RangeTypeBytes.key	=> RangeTypeBytes
			}
			
	def unparse(it:RangeType):String	=
			it.key
}

sealed trait RangeType {
	def key:String
}
case object RangeTypeBytes	extends RangeType {
	val key	= "bytes"
}
