package scwebapp.data

import scwebapp.format._
import scparse.ng.text._

object ContentEncodingType {
	lazy val parser:TextParser[ContentEncodingType]	= parsers.value

	def unparse(it:ContentEncodingType):String	=
		it match {
			case Gzip		=> "gzip"
			case Compress	=> "compress"
			case Deflate	=> "deflate"
			case Br			=> "br"
		}

	private object parsers {
		import HttpParsers._

		val value:TextParser[ContentEncodingType]	=
			token
			.map	(CaseUtil.lowerCase)
			.requirePartial[ContentEncodingType] {
				case "gzip"		=> Gzip
				case "compress"	=> Compress
				case "deflate"	=> Deflate
				case "br"		=> Br
			}
			.named ("ContentEncodingType")
	}

	//------------------------------------------------------------------------------

	// NOTE no identity here, that's only allowed for acceptance checks
	case object Gzip		extends ContentEncodingType
	case object Compress	extends ContentEncodingType
	case object Deflate		extends ContentEncodingType
	case object Br			extends ContentEncodingType
}

sealed trait ContentEncodingType
