package scwebapp

import java.nio.charset.Charset

import scutil.lang.*
import scutil.codec.*

final case class HttpPath(raw:String) {
	def decoded(encoding:Charset):Either[URIComponentProblem,String]	=
		URIComponent.forCharset(encoding).decode(raw)

	final def utf8:Either[URIComponentProblem,String]	=
		decoded(Charsets.utf_8)
}
