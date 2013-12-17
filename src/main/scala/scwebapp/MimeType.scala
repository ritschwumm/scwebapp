package scwebapp

import scala.annotation.tailrec

import scutil.lang._



/*
top-level:	text image audio video application multipart message
*/

object MimeType {
	val emptyParameters	= NoCaseParameters.empty
	
	def parse(s:String):Option[MimeType]	=
			HttpUtil parseContentType s map {
				case ((major, minor), params)	=> MimeType(major, minor, params)
			}
}

case class MimeType(major:String, minor:String, parameters:NoCaseParameters = MimeType.emptyParameters) {
	def value:String =
			major + "/" + minor + (
				parameters.all map { case (key,value) => "; " + key + "=" + value } mkString ""
			)
			
	def addParameter(name:String, value:String):MimeType	=
			copy(parameters = parameters append (name, value))
}
