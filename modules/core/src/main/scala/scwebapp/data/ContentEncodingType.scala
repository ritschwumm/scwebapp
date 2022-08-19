package scwebapp.data

import scwebapp.format.*
import scparse.ng.text.*

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
		import HttpParsers.*

		val value:TextParser[ContentEncodingType]	=
			token
			.map	(CaseUtil.lowerCase)
			.collect[ContentEncodingType] {
				case "gzip"		=> Gzip
				case "compress"	=> Compress
				case "deflate"	=> Deflate
				case "br"		=> Br
			}
			.named ("ContentEncodingType")
	}
}

enum ContentEncodingType {
	// NOTE no identity here, that's only allowed for acceptance checks
	case Gzip
	case Compress
	case Deflate
	case Br
}
