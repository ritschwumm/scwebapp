package scwebapp.data

import scutil.implicits._

import scwebapp.format.CaseUtil

object DispositionType {
	def parse(it:String):Option[DispositionType]	=
			CaseUtil lowerCase it matchOption {
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
