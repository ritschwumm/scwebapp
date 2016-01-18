package scwebapp

import scutil.lang._
import scutil.implicits._

object DispositionType {
	val prism:Prism[String,DispositionType]	=
			Prism(parse, unparse)
		
	def parse(it:String):Option[DispositionType]	=
			it.toLowerCase matchOption {
				case "attachment"	=> DispositionAttachment
				case "inline"		=> DispositionInline
			}
			
	def unparse(it:DispositionType):String	=
			it match {
				case DispositionAttachment	=> "attachment"
				case DispositionInline		=> "inline"
			}
}

sealed trait DispositionType
case object DispositionAttachment	extends DispositionType
case object DispositionInline		extends DispositionType
