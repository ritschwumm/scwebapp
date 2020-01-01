package scwebapp.header

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scparse.ng.text._

object LastModified extends HeaderType[LastModified] {
	val key	= "Last-Modified"

	def parse(it:String):Option[LastModified]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:LastModified):String	=
		HttpDate unparse it.value

	private object parsers {
		import HttpParsers._

		val value:TextParser[LastModified]	= dateValue map LastModified.apply
		val finished:TextParser[LastModified]	= value finish LWSP
	}
}

final case class LastModified(value:HttpDate)
