package scwebapp.data

import scutil.base.implicits._

import scwebapp.format._
import scparse.ng.text._

object MediaPattern {
	lazy val parser:TextParser[MediaPattern]	= parsers.value

	def unparse(it:MediaPattern):String	=
			it match {
				case WildWild					=> "*/*"
				case TypeWild(major)			=> show"${major}/*"
				case TypeSubtype(major, minor)	=> show"${major}/${minor}"
			}

	private object parsers {
		import HttpParsers._

		val major:TextParser[String]	= token
		val minor:TextParser[String]	= token

		val wildWild:TextParser[MediaPattern]		= symbolN("*/*") tag WildWild
		val typeWild:TextParser[MediaPattern]		= major left symbol('/') left symbol('*') map TypeWild.apply
		val typeSubtype:TextParser[MediaPattern]	= major left symbol('/') next minor map TypeSubtype.tupled

		val value:TextParser[MediaPattern]			= wildWild orElse typeWild orElse typeSubtype eatLeft LWSP
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
