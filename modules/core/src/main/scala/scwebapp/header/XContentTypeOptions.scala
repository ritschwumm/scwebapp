package scwebapp.header

import scwebapp.HeaderType

// TODO not typesafe

object XContentTypeOptions extends HeaderType[XContentTypeOptions] {
	val key	= "X-Content-Type-Options"

	def parse(it:String):Option[XContentTypeOptions]	=
			Some(XContentTypeOptions(it))

	def unparse(it:XContentTypeOptions):String	=
			it.value
}

// "nosniff"
final case class XContentTypeOptions(value:String)
