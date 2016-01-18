package scwebapp

object ContentEncodingValue {
	def unparse(it:ContentEncodingValue):String	=
			it match {
				case ContentEncodingGzip		=> "gzip"
				case ContentEncodingCompress	=> "compress"
				case ContentEncodingDeflate		=> "deflate"
				case ContentEncodingBr			=> "br"
			}
			
}

// NOTE no identity here, that's only allowed for acceptance checks
sealed trait ContentEncodingValue
case object ContentEncodingGzip		extends ContentEncodingValue
case object ContentEncodingCompress	extends ContentEncodingValue
case object ContentEncodingDeflate	extends ContentEncodingValue
case object ContentEncodingBr		extends ContentEncodingValue
