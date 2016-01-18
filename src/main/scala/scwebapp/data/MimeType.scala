package scwebapp.data

import scala.annotation.tailrec

import scutil.implicits._

import scwebapp.format.HttpParser

// top-level:	text image audio video application multipart message

// TODO handle trees
object MimeType {
	def parse(s:String):Option[MimeType]	=
			HttpParser parseContentType s
		
	def unparse(it:MimeType):String	=
			it.major + "/" + it.minor + (
				it.parameters.all
				.map		{ case (key, value) => so"; ${key}=${value}" }
				.mkString	("")
			)
}

case class MimeType(major:String, minor:String, parameters:NoCaseParameters = NoCaseParameters.empty) {
	def value:String =
			MimeType unparse this
			
	def addParameter(name:String, value:String):MimeType	=
			copy(parameters = parameters append (name, value))
		
	def sameMajorAndMinor(that:MimeType):Boolean	=
			this.major == that.major &&
			this.minor == that.minor
}
