package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object ContentEncodingType {
	lazy val parser:CParser[ContentEncodingType]	= parsers.value

	def unparse(it:ContentEncodingType):String	=
			it match {
				case Gzip		=> "gzip"
				case Compress	=> "compress"
				case Deflate	=> "deflate"
				case Br			=> "br"
			}

	private object parsers {
		import HttpParsers._

		val value:CParser[ContentEncodingType]	=
				token map CaseUtil.lowerCase collect {
					case "gzip"		=> Gzip
					case "compress"	=> Compress
					case "deflate"	=> Deflate
					case "br"		=> Br
				}
	}

	//------------------------------------------------------------------------------

	// NOTE no identity here, that's only allowed for acceptance checks
	case object Gzip		extends ContentEncodingType
	case object Compress	extends ContentEncodingType
	case object Deflate		extends ContentEncodingType
	case object Br			extends ContentEncodingType
}

sealed trait ContentEncodingType
