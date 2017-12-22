package scwebapp.header

import scwebapp.HeaderType

// TODO not typesafe

object XUACompatible extends HeaderType[XUACompatible] {
	val key	= "X-UA-Compatible"
	
	def parse(it:String):Option[XUACompatible]	=
			Some(XUACompatible(it))
		
	def unparse(it:XUACompatible):String	=
			it.value
}

// "IE=edge"
final case class XUACompatible(value:String)
