package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object ContentEncodingType {
	lazy val parser:CParser[ContentEncodingType]	= parsers.value

	def unparse(it:ContentEncodingType):String	=
			it match {
				case ContentEncodingGzip		=> "gzip"
				case ContentEncodingCompress	=> "compress"
				case ContentEncodingDeflate		=> "deflate"
				case ContentEncodingBr			=> "br"
			}

	private object parsers {
		import HttpParsers._

		val value:CParser[ContentEncodingType]	=
				token map CaseUtil.lowerCase collect {
					case "gzip"		=> ContentEncodingGzip
					case "compress"	=> ContentEncodingCompress
					case "deflate"	=> ContentEncodingDeflate
					case "br"		=> ContentEncodingBr
				}
	}
}

// NOTE no identity here, that's only allowed for acceptance checks
sealed trait ContentEncodingType
case object ContentEncodingGzip		extends ContentEncodingType
case object ContentEncodingCompress	extends ContentEncodingType
case object ContentEncodingDeflate	extends ContentEncodingType
case object ContentEncodingBr		extends ContentEncodingType
