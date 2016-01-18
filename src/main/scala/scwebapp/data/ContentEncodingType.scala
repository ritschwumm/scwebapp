package scwebapp.data

object ContentEncodingType {
	def unparse(it:ContentEncodingType):String	=
			it match {
				case ContentEncodingGzip		=> "gzip"
				case ContentEncodingCompress	=> "compress"
				case ContentEncodingDeflate		=> "deflate"
				case ContentEncodingBr			=> "br"
			}
			
}

// NOTE no identity here, that's only allowed for acceptance checks
sealed trait ContentEncodingType
case object ContentEncodingGzip		extends ContentEncodingType
case object ContentEncodingCompress	extends ContentEncodingType
case object ContentEncodingDeflate	extends ContentEncodingType
case object ContentEncodingBr		extends ContentEncodingType
