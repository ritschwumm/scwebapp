package scwebapp.data

import scutil.base.implicits._

import scwebapp.format._
import scwebapp.parser.string._

object MediaPattern {
	lazy val parser:CParser[MediaPattern]	= parsers.value
		
	def unparse(it:MediaPattern):String	=
			it match {
				case MediaWildWild					=> "*/*"
				case MediaTypeWild(major)			=> so"${major}/*"
				case MediaTypeSubtype(major, minor)	=> so"${major}/${minor}"
			}
			
	private object parsers {
		import HttpParsers._
		
		val major:CParser[String]	= token
		val minor:CParser[String]	= token
		
		val wildWild:CParser[MediaPattern]		= symbolN("*/*") tag MediaWildWild
		val typeWild:CParser[MediaPattern]		= major left symbol('/') left symbol('*') map MediaTypeWild.apply
		val typeSubtype:CParser[MediaPattern]	= major left symbol('/') next minor map MediaTypeSubtype.tupled
		
		val value:CParser[MediaPattern]			= wildWild orElse typeWild orElse typeSubtype eating LWSP
	}
}

sealed trait MediaPattern {
	// returns rank, if any
	def matches(typ:MimeType):Option[Int]	=
			this matchOption {
				case MediaWildWild																=> 0
				case MediaTypeWild(major)			if major == typ.major						=> 1
				case MediaTypeSubtype(major, minor)	if major == typ.major && minor == typ.minor	=> 2
			}
}
final case object MediaWildWild									extends MediaPattern
final case class MediaTypeWild(major:String)					extends MediaPattern
final case class MediaTypeSubtype(major:String, minor:String)	extends MediaPattern
