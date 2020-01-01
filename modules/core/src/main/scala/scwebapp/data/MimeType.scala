package scwebapp.data

import java.nio.charset.Charset

import scutil.base.implicits._
import scutil.lang._

import scwebapp.format._
import scparse.ng.text._

// top-level:	text image audio video application multipart message

// TODO handle trees
object MimeType {
	lazy val parser:TextParser[MimeType]	=
		parsers.value

	// NOTE this is special, because MimeType is used outside a ContentType, too
	def parse(it:String):Option[MimeType]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:MimeType):String	=
		it.major + "/" + it.minor +
		(HttpUnparsers parameterList it.parameters)

	private object parsers {
		import HttpParsers._

		val major:TextParser[String]			= token
		val minor:TextParser[String]			= token
		val typ:TextParser[(String,String)]	= major left symbol('/') next minor

		val value:TextParser[MimeType]	=
			typ next parameterList map {
				case ((major, minor), params) => MimeType(major, minor, params)
			}

		val finished:TextParser[MimeType]	= value finish LWSP
	}
}

final case class MimeType(major:String, minor:String, parameters:NoCaseParameters = NoCaseParameters.empty) {
	def value:String =
		MimeType unparse this

	def addParameter(name:String, value:String):MimeType	=
		copy(parameters = parameters append (name, value))

	def sameMajorAndMinor(that:MimeType):Boolean	=
		this.major == that.major &&
		this.minor == that.minor

	def charset:Either[String,Option[Charset]]	=
		(parameters firstString "charset")
		.map { it =>
			Charsets byName it leftMap constant(show"invalid charset ${it}")
		}
		.sequenceEither
}
