package scwebapp

import scala.annotation.tailrec

import scutil.lang._

// @see http://tools.ietf.org/html/rfc2045#section-5.1
// @see http://tools.ietf.org/html/rfc2046
// @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1

/*
top-level:	text image audio video application multipart message
*/

// TODO parse quoted values

object MimeType {
	val emptyParameters	= NoCaseParameters.empty
	
	private val	fullRE	= """([a-z]+|\*)/([a-z]+|\*)(.*)""".r
	def parse(s:String):Option[MimeType]	=
			for {
				groups					<- fullRE unapplySeq s
				Seq(major, minor, rest)	= groups
				params					<- parseParams(rest)
			}
			yield MimeType(major, minor, params)
	
	private val paramRE	= """;\s*([^;= ]+)\s*=\s*([^;= ]*)\s*""".r
	def parseParams(s:String):Option[NoCaseParameters]	= {
		@tailrec
		def loop(ss:CharSequence, out:NoCaseParameters):Option[NoCaseParameters]	= {
			if (ss.length == 0)	Some(out)
			else {
				paramRE findPrefixMatchOf ss match {
					case Some(matched)	=>
						val name	= matched group 1
						val value	= matched group 2
						val rest	= Option(matched.after) getOrElse ""
						loop(rest, out append (name, value))
					case None	=> 
						None
				}
			}
		}
		loop(s.trim, emptyParameters)
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
