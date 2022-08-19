package scwebapp.data

import scutil.core.implicits.*

import scwebapp.format.*
import scparse.ng.text.*

object MediaPattern {
	lazy val parser:TextParser[MediaPattern]	= parsers.value

	def unparse(it:MediaPattern):String	=
		it match {
			case WildWild					=> "*/*"
			case TypeWild(major)			=> show"${major}/*"
			case TypeSubtype(major, minor)	=> show"${major}/${minor}"
		}

	private object parsers {
		import HttpParsers.*

		val major:TextParser[String]	= token
		val minor:TextParser[String]	= token

		val wildWild:TextParser[MediaPattern]		= symbolN("*/*") tag WildWild
		val typeWild:TextParser[MediaPattern]		= major left symbol('/') left symbol('*') map TypeWild.apply
		val typeSubtype:TextParser[MediaPattern]	= major left symbol('/') next minor map TypeSubtype.apply.tupled

		val value:TextParser[MediaPattern]			= wildWild orElse typeWild orElse typeSubtype eatLeft LWSP
	}
}

enum MediaPattern {
	case WildWild
	case TypeWild(major:String)
	case TypeSubtype(major:String, minor:String)

	// returns rank, if any
	def matches(typ:MimeType):Option[Int]	=
		this matchOption {
			case MediaPattern.WildWild																=> 0
			case MediaPattern.TypeWild(major)			if major == typ.major						=> 1
			case MediaPattern.TypeSubtype(major, minor)	if major == typ.major && minor == typ.minor	=> 2
		}
}
