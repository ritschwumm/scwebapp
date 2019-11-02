package scwebapp.data

import scutil.base.implicits._

import scwebapp.format._
import scwebapp.parser.string._

object MediaPattern {
	lazy val parser:CParser[MediaPattern]	= parsers.value

	def unparse(it:MediaPattern):String	=
			it match {
				case WildWild					=> "*/*"
				case TypeWild(major)			=> show"${major}/*"
				case TypeSubtype(major, minor)	=> show"${major}/${minor}"
			}

	private object parsers {
		import HttpParsers._

		val major:CParser[String]	= token
		val minor:CParser[String]	= token

		val wildWild:CParser[MediaPattern]		= symbolN("*/*") tag WildWild
		val typeWild:CParser[MediaPattern]		= major left symbol('/') left symbol('*') map TypeWild.apply
		val typeSubtype:CParser[MediaPattern]	= major left symbol('/') next minor map TypeSubtype.tupled

		val value:CParser[MediaPattern]			= wildWild orElse typeWild orElse typeSubtype eating LWSP
	}

	//------------------------------------------------------------------------------

	final case object WildWild									extends MediaPattern
	final case class TypeWild(major:String)						extends MediaPattern
	final case class TypeSubtype(major:String, minor:String)	extends MediaPattern
}

sealed trait MediaPattern {
	// returns rank, if any
	def matches(typ:MimeType):Option[Int]	=
			this matchOption {
				case MediaPattern.WildWild																=> 0
				case MediaPattern.TypeWild(major)			if major == typ.major						=> 1
				case MediaPattern.TypeSubtype(major, minor)	if major == typ.major && minor == typ.minor	=> 2
			}
}
