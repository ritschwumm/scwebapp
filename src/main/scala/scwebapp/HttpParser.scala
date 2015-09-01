package scwebapp

import scutil.lang._

import scwebapp.parser.Parser._
import scwebapp.parser.string._

object HttpParser {
	def parseContentDisposition(it:String):Option[(String,NoCaseParameters)]	=
			(contentDisposition parseStringOption it)
			.map { case (kind, params) => (kind, NoCaseParameters(params)) }
		
	def parseContentType(it:String):Option[MimeType]	=
			(contentType parseStringOption it)
			.map { case ((major, minor), params) => MimeType(major, minor, NoCaseParameters(params)) }
		
	def parseCookie(it:String):Option[CaseParameters]	=
			(cookieHeader parseStringOption it)
			.map (CaseParameters.apply)
			
	//==============================================================================
	
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
	val LWS:CParser[Char]		= CRLF.option next (SP orElse HT).nes tag ' '
	val TEXT:CParser[Char]		= LWS orElse (CTL.prevent right OCTET)
	
	val WSP:CParser[Char]		= SP orElse HT
	val OWS:CParser[Unit]		= (CRLF.option next WSP).seq tag (())
	
	def symbol(c:Char):CParser[Char]		= is(c) token LWS
	def symbolN(s:String):CParser[String]	= sis(s) token LWS
	
	// NOTE separator is tspecials and "{} \t"
	val separator:CParser[Char]	= in("()<>@,;:\\\"/[]?={} \t")
	val token:CParser[String]	= ((separator orElse CTL).prevent right CHAR).nes.stringify token LWS
	
	// val ctext:Parser[Char]		= (sat('(') orElse sat(')')).prevent right TEXT
	// val comment:Parser[String]	= '(' ~> (quoted_pair_string | comment | ctext_string).* <~ ')' stringify
	
	// TODO check, @see HttpUtil.quote
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
	val quotedString:CParser[String]	= (DQ right quotedChar.seq left DQ).stringify token LWS

	val hashSepa:CParser[Char]	= symbol(',')
	def hash[T](sub:CParser[T]):CParser[ISeq[T]]	= sub sepSeq	hashSepa
	def hash1[T](sub:CParser[T]):CParser[Nes[T]]	= sub sepNes	hashSepa
	
	//------------------------------------------------------------------------------
		
	val attribute:CParser[String]				= token
	val value:CParser[String]					= token orElse quotedString
	val parameter:CParser[(String,String)]		= attribute left symbol('=') next value
	val nextParameter:CParser[(String,String)]	= symbol(';') right parameter
		
	//------------------------------------------------------------------------------
	
	val kind:CParser[String]	= token
	
	val contentDisposition:CParser[(String,ISeq[(String,String)])]	=
			kind next nextParameter.seq finish LWS
		
	//------------------------------------------------------------------------------
	
	val major:CParser[String]			= token
	val minor:CParser[String]			= token
	val typ:CParser[(String,String)]	= major left symbol('/') next minor
	
	val contentType:CParser[((String,String),ISeq[(String,String)])]	=
			typ next nextParameter.seq finish LWS
	
	//------------------------------------------------------------------------------
	
	val bytesUnit:CParser[String]									= symbolN("bytes")
	val bytePos:CParser[Long]										= DIGIT.nes.stringify map { _.toLong } token LWS
	val byteRangeSpec:CParser[(Long,Option[Long])]					= bytePos left symbol('-') next bytePos.option
	val suffixByteRangeSpec:CParser[Long]							= symbol('-') right bytePos
	val byteRangeOne:CParser[Either[(Long,Option[Long]),Long]]		= byteRangeSpec either suffixByteRangeSpec
	val byteRangeSet:CParser[Nes[Either[(Long,Option[Long]),Long]]]	= hash1(byteRangeOne) finish LWS
	
	val rangeHeader:CParser[Nes[Either[(Long,Option[Long]),Long]]]	=
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
	
	val cookieHeader:CParser[ISeq[(String,String)]]	= OWS right cookieString left OWS
}
