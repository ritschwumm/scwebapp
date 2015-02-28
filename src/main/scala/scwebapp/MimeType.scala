package scwebapp

import scala.annotation.tailrec

// top-level:	text image audio video application multipart message

object MimeType {
	val emptyParameters	= NoCaseParameters.empty
	
	def parse(s:String):Option[MimeType]	=
			HttpParser parseContentType s
		
	def unparse(it:MimeType):String	=
			it.major + "/" + it.minor + (
				it.parameters.all
				.map		{ case (key, value) => s"; ${key}=${value}" }
				.mkString	("")
			)
}

case class MimeType(major:String, minor:String, parameters:NoCaseParameters = MimeType.emptyParameters) {
	def value:String =
			MimeType unparse this
			
	def addParameter(name:String, value:String):MimeType	=
			copy(parameters = parameters append (name, value))
}
