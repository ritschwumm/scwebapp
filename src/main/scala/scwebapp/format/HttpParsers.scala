package scwebapp.format

import java.nio.charset.Charset

import scutil.lang._
import scutil.io.Base64
import scutil.io.Charsets

import scwebapp.data._
import scwebapp.parser.Parser._
import scwebapp.parser.string._

private[format] object HttpParsers {
	val OCTET:CParser[Char]		= rng(0, 255)
	val CHAR:CParser[Char]		= rng(0, 127)
	val UPALPHA:CParser[Char]	= rng('A', 'Z')
	val LOALPHA:CParser[Char]	= rng('a', 'z')
	val ALPHA:CParser[Char]		= UPALPHA orElse LOALPHA
	val DIGIT:CParser[Char]		= rng('0', '9')
	val HEXDIG:CParser[Char]	= DIGIT orElse rng('a', 'f') orElse rng('A', 'F')
	val CTL:CParser[Char]		= rng(0, 31) orElse is(127)
	val CR:CParser[Char]		= is('\r')
	val LF:CParser[Char]		= is('\n')
	val SP:CParser[Char]		= is(' ')
	val HT:CParser[Char]		= is('\t')
	val DQ:CParser[Char]		= is('"')
	
	val CRLF:CParser[String]	= CR next LF map { case (a, b) => a.toString + b.toString }
	val WSP:CParser[Char]		= SP orElse HT
	val LWS:CParser[Char]		= CRLF.option next WSP.nes tag ' '
	val TEXT:CParser[Char]		= LWS orElse (CTL.prevent right OCTET)
	// TODO in the spec, OWS does not include CRLF
	val OWS:CParser[Unit]		= (CRLF.option next WSP).seq tag (())
	
	def symbol(c:Char):CParser[Char]		= is(c) token LWS
	def symbolN(s:String):CParser[String]	= sis(s) token LWS
	
	// NOTE separator is tspecials and "{} \t"
	val separator:CParser[Char]	= in("()<>@,;:\\\"/[]?={} \t")
	val token:CParser[String]	= ((separator orElse CTL).prevent right CHAR).nes.stringify token LWS
	
	// val ctext:Parser[Char]		= (sat('(') orElse sat(')')).prevent right TEXT
	// val comment:Parser[String]	= '(' ~> (quoted_pair_string | comment | ctext_string).* <~ ')' stringify
	
	// TODO check, @see HeaderUnparsers.quote
	val quotedPair:CParser[Char]	=
			is('\\') right CHAR collect {
				case 'r'	=> '\r'
				case 'n'	=> '\n'
				// case '"'		=> '"'
				// case '\\'	=> '\\'
				case x		=> x
			}
	
	val dqText:CParser[Char]			= DQ.prevent right TEXT
	val quotedChar:CParser[Char]		= quotedPair orElse dqText
	val quotedString:CParser[String]	= (quotedChar.seq inside DQ).stringify token LWS

	val hashSepa:CParser[Char]	= symbol(',')
	def hash[T](sub:CParser[T]):CParser[ISeq[T]]	= sub sepSeq	hashSepa
	def hash1[T](sub:CParser[T]):CParser[Nes[T]]	= sub sepNes	hashSepa
	
	//------------------------------------------------------------------------------
	
	/*
	val attribute:CParser[String]				= token
	val value:CParser[String]					= token orElse quotedString
	val parameter:CParser[(String,String)]		= attribute left symbol('=') next value
	val nextParameter:CParser[(String,String)]	= symbol(';') right parameter
	val parameterList:CParser[NoCaseParameters]	= nextParameter.seq map NoCaseParameters.apply
	*/
	
	// TODO is this allowed for all parameters, or just for content-disposition?
	// NOTE this is _not_ allowed for content-disposition in multipart/form-data
	
	val attrChar:CParser[Char]		= ALPHA orElse DIGIT orElse in("!#$&+-.^_`|~")
	val parmname:CParser[String]	= attrChar.nes.stringify
	
	val value:CParser[String]		= token orElse quotedString
	val regParameter:CParser[(Boolean,(String,String))]	=
			(parmname token WSP) left symbol('=') next value map { false -> _ }
	
	// @see https://tools.ietf.org/html/rfc5987
	
	val hexDigNibble:CParser[Int]	=
			HEXDIG map {
				case x if x >= '0'	&& x <= '9'	=> x - '0' + 0
				case x if x >= 'a'	&& x <= 'f'	=> x - 'a' + 10
				case x if x >= 'A'	&& x <= 'F'	=> x - 'A' + 10
			}
	val hexByte:CParser[Byte]	=
			(hexDigNibble next hexDigNibble) map { case (h,l) =>
				((h << 4) | l).toByte
			}
	val pctEncoded:CParser[Byte]			= is('%') right hexByte
	val attrCharByte:CParser[Byte]			= attrChar map { _.toByte }
	val valueCharBytes:CParser[Array[Byte]]	= (pctEncoded orElse attrCharByte).seq map { _.toArray }
	
	val mimeCharsetC:CParser[Char]			= ALPHA orElse DIGIT orElse in("!#$%&+-^_`{}~")
	val mimeCharset:CParser[String]			= mimeCharsetC.nes.stringify
	
	// TODO hack
	val extValuePart:CParser[String]		= mimeCharsetC.seq.stringify
	
	// NOTE the rfc grammar requires uppercase, but the examples do not
	val simpleCharset:CParser[Charset]	=
			extValuePart map { CaseUtil.lowerCase } collect {
				case "utf-8"		=> Charsets.utf_8
				case "iso-8859-1"	=> Charsets.iso_8859_1
			}
			
	// NOTE this ignores a mimeCharset
	val charset:CParser[Option[Charset]]	=
			(simpleCharset	map Some.apply)	orElse
			(mimeCharset	tag None)
			
	// TODO hack
	val language:CParser[String]	= extValuePart
	
	val extValueOpt:CParser[Option[String]]	=
			for {
				charset		<- charset
				_			<- cis('\'')
				language	<- language
				_			<- cis('\'')
				bytes		<- valueCharBytes
			}
			yield {
				charset map StringDecoder.decodeOption flatMap { _ apply bytes }
			}
	
	val extValue:CParser[String]	= extValueOpt.filterSome
			
	val extParName:CParser[String]	= parmname left cis('*')
		
	val extParameter:CParser[(Boolean,(String,String))]	=
			extParName token WSP left symbol('=') next extValue map { true -> _ }
	
	val parameter:CParser[(Boolean,(String,String))]			= (regParameter orElse extParameter) token LWS
	val nextParameter:CParser[(Boolean,(String,String))]		= symbol(';') right parameter
	val manyParameters:CParser[ISeq[(Boolean,(String,String))]]	= nextParameter.seq
	
	// NOTE moves extended parameters to the front
	val parameterList:CParser[NoCaseParameters]	=
			manyParameters map { list =>
				val regs	= list collect { case (false,	kv) => kv }
				val exts	= list collect { case (true,	kv) => kv }
				NoCaseParameters(exts ++ regs)
			}
	
	//------------------------------------------------------------------------------

	val longZero:CParser[Long]	= cis('0') tag 0L
	val longPositive:CParser[Long]	=
			rng('1', '9') next DIGIT.seq map {
				case (h, t)	=> (h +: t).toString.toLong
			}
	val longUnsigned:CParser[Long]	= longZero orElse longPositive
		
	val contentLength:CParser[Long]	= longUnsigned.phrase
		
	//------------------------------------------------------------------------------
	
	val kind:CParser[DispositionType]	= token filterMap DispositionType.parse
	
	val contentDisposition:CParser[Disposition]	=
			kind next parameterList finish LWS map {
				case (kind, params)	=>
					Disposition(
						kind,
						params firstString "filename"
					)
			}
		
	//------------------------------------------------------------------------------
	
	val major:CParser[String]			= token
	val minor:CParser[String]			= token
	val typ:CParser[(String,String)]	= major left symbol('/') next minor
	
	val contentType:CParser[MimeType]	=
			typ next parameterList finish LWS map {
				case ((major, minor), params) => MimeType(major, minor, params)
			}
	
	//------------------------------------------------------------------------------
	
	val bytesUnit:CParser[String]					= symbolN(RangeTypeBytes.key)
	val bytePos:CParser[Long]						= DIGIT.nes.stringify map { _.toLong } token LWS
	val byteRangeSpec:CParser[(Long,Option[Long])]	= bytePos left symbol('-') next bytePos.option
	val suffixByteRangeSpec:CParser[Long]			= symbol('-') right bytePos
	val byteRangeOne:CParser[RangeValue]			=
			byteRangeSpec either suffixByteRangeSpec map {
				case Left((a, None))	=> RangeBegin(a)
				case Left((a, Some(b)))	=> RangeFromTo(a, b)
				case Right(b)			=> RangeEnd(b)
			}
	val byteRangeSet:CParser[Nes[RangeValue]]		= hash1(byteRangeOne) finish LWS
	
	val rangeHeader:CParser[Nes[RangeValue]]	=
			bytesUnit right symbol('=') right byteRangeSet
		
	//------------------------------------------------------------------------------
	
	val cookieName:CParser[String]	= token
	val cookieOctet:CParser[Char]	=
			cis(0x21)		orElse
			rng(0x23, 0x2b)	orElse
			rng(0x2d, 0x3a)	orElse
			rng(0x3c, 0x5b)	orElse
			rng(0x5d, 0x7e)
	val cookieValueRaw:CParser[String]				= cookieOctet.seq.stringify
	val cookieValueQuoted:CParser[String]			= cookieValueRaw inside DQ
	val cookieValue:CParser[String]					= cookieValueRaw orElse cookieValueQuoted
	val cookiePair:CParser[(String,String)]			= cookieName left is('=') next cookieValue
	val cookieString:CParser[ISeq[(String,String)]]	= cookiePair sepSeq (is(';') next SP)
	
	val cookieHeader:CParser[CaseParameters]		= cookieString inside OWS map CaseParameters.apply

	//------------------------------------------------------------------------------

	val base64Char:CParser[Char]	=
			ALPHA orElse DIGIT orElse in("+/=")
			
	def base64(charset:Charset):CParser[String]	=
			(base64Char).seq.stringify filterMap Base64.decode filterMap (StringDecoder decodeOption charset)
	
	val userid:CParser[String]	=
			(TEXT filter { _ != ':' }).seq.stringify
	
	val password:CParser[String]	=
			TEXT.seq.stringify
	
	val basicCredentials:CParser[BasicCredentials]	=
			userid left is(':') next password map BasicCredentials.tupled
		
	def basicAuthentication(charset:Charset):CParser[BasicCredentials]	=
			(symbolN("Basic") right base64(charset) nestString basicCredentials).phrase
}
