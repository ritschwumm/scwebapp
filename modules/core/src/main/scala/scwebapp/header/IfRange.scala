package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

object IfRange extends HeaderType[IfRange] {
	val key	= "If-Range"

	def parse(it:String):Option[IfRange]	=
			parsers.finished parseStringOption it

	def unparse(it:IfRange):String	=
			IfRangeValue unparse it.value

	private object parsers {
		import HttpParsers._

		val value:CParser[IfRange]		= IfRangeValue.parser map IfRange.apply
		val finished:CParser[IfRange]	= value finish LWSP
	}
}

final case class IfRange(value:IfRangeValue) {
	def needsFull(eTag:ETagValue, lastModified:HttpDate):Boolean	=
			value needsFull (eTag, lastModified)
}
