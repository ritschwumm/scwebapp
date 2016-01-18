package scwebapp
package format

import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._
import scutil.io.Charsets
import scutil.io.Base64

import scwebapp.parser.Parser._
import scwebapp.parser.string._

object HttpParser {
	def parseContentLength(it:String):Option[Long]	=
			it guardBy { _ matches "\\d+" } flatMap { _.toLongOption }
		
	// TODO ugly
	def parseContentDisposition(it:String):Option[Disposition]	=
			(contentDisposition parseStringOption it)
			.flatMap { case (kind, params) =>
				DispositionType parse kind map { typ =>
					val ncp	= NoCaseParameters(params)
					Disposition(
						typ,
						ncp firstString "filename",
						ncp firstString "filename*"
					)
				}
			}
		
	def parseContentType(it:String):Option[MimeType]	=
			(contentType parseStringOption it)
			.map { case ((major, minor), params) => MimeType(major, minor, NoCaseParameters(params)) }
		
	def parseCookie(it:String):Option[CaseParameters]	=
			(cookieHeader parseStringOption it)
			.map (CaseParameters.apply)
			
	def parseEncoding(name:String):Option[Charset]	=
			(Charsets byName name).toOption
		
	def parseBasicAuthentication(header:String, encoding:Charset):Tried[String,BasicAuthentication]	=
			for {
				code	<- header cutPrefix "Basic "						toWin	so"missing Basic prefix in ${header}"
				bytes	<- Base64 decode code								toWin	so"invalid base64 code in ${code}"
				str		<- Catch.exception in (new String(bytes, encoding))	mapFail	constant("invalid string bytes")
				pair	<- str splitAroundFirstChar ':'						toWin	so"missing colon separator in ${str}"
			}
			yield BasicAuthentication tupled pair
			
	// NOTE this only includes valid ranges
	def parseRangeHeader(total:Long)(value:String):Option[ISeq[RequestRange]]	=
			(rangeHeader parseStringOption value)
			.map	{ _.toVector flatMap parseHttpRange(total) }
	
	private def parseHttpRange(total:Long)(it:OneOrTwo[Long,Long]):Option[RequestRange]	= {
		val last	= total - 1
		it matchOption {
			case OneAndTwo(start, end)	if start >= 0 && start <= last && end < last	=> RequestRange(InclusiveRange(start,			end),	total)
			case One(start)				if start >= 0 && start <= last					=> RequestRange(InclusiveRange(start,			last),	total)
			case Two(count)				if count > 0  && count <= total					=> RequestRange(InclusiveRange(total - count,	last),	total)
		}
	}
			
	//==============================================================================
	
	private[format] val OCTET:CParser[Char]		= rng(0, 255)
	private[format] val CHAR:CParser[Char]		= rng(0, 127)
	private[format] val UPALPHA:CParser[Char]	= rng('A', 'Z')
	private[format] val LOALPHA:CParser[Char]	= rng('a', 'z')
	private[format] val ALPHA:CParser[Char]		= UPALPHA orElse LOALPHA
	private[format] val DIGIT:CParser[Char]		= rng('0', '9')
	private[format] val HEXDIG:CParser[Char]	= DIGIT orElse rng('a', 'f') orElse rng('A', 'F')
	private[format] val CTL:CParser[Char]		= rng(0, 31) orElse is(127)
	private[format] val CR:CParser[Char]		= is('\r')
	private[format] val LF:CParser[Char]		= is('\n')
	private[format] val SP:CParser[Char]		= is(' ')
	private[format] val HT:CParser[Char]		= is('\t')
	private[format] val DQ:CParser[Char]		= is('"')
	
	private[format] val CRLF:CParser[String]	= CR next LF map { case (a, b) => a.toString + b.toString }
	private[format] val LWS:CParser[Char]		= CRLF.option next (SP orElse HT).nes tag ' '
	private[format] val TEXT:CParser[Char]		= LWS orElse (CTL.prevent right OCTET)
	
	private[format] val WSP:CParser[Char]		= SP orElse HT
	private[format] val OWS:CParser[Unit]		= (CRLF.option next WSP).seq tag (())
	
	private[format] def symbol(c:Char):CParser[Char]		= is(c) token LWS
	private[format] def symbolN(s:String):CParser[String]	= sis(s) token LWS
	
	// NOTE separator is tspecials and "{} \t"
	private[format] val separator:CParser[Char]	= in("()<>@,;:\\\"/[]?={} \t")
	private[format] val token:CParser[String]	= ((separator orElse CTL).prevent right CHAR).nes.stringify token LWS
	
	// private[format] val ctext:Parser[Char]		= (sat('(') orElse sat(')')).prevent right TEXT
	// private[format] val comment:Parser[String]	= '(' ~> (quoted_pair_string | comment | ctext_string).* <~ ')' stringify
	
	// TODO check, @see HeaderUnparsers.quote
	private[format] val quotedPair:CParser[Char]	=
			is('\\') right CHAR collect {
				case 'r'	=> '\r'
				case 'n'	=> '\n'
				// case '"'		=> '"'
				// case '\\'	=> '\\'
				case x		=> x
			}
	
	private[format] val dqText:CParser[Char]			= DQ.prevent right TEXT
	private[format] val quotedChar:CParser[Char]		= quotedPair orElse dqText
	private[format] val quotedString:CParser[String]	= (quotedChar.seq inside DQ).stringify token LWS

	private[format] val hashSepa:CParser[Char]	= symbol(',')
	private[format] def hash[T](sub:CParser[T]):CParser[ISeq[T]]	= sub sepSeq	hashSepa
	private[format] def hash1[T](sub:CParser[T]):CParser[Nes[T]]	= sub sepNes	hashSepa
	
	//------------------------------------------------------------------------------
		
	private[format] val attribute:CParser[String]				= token
	private[format] val value:CParser[String]					= token orElse quotedString
	private[format] val parameter:CParser[(String,String)]		= attribute left symbol('=') next value
	private[format] val nextParameter:CParser[(String,String)]	= symbol(';') right parameter
		
	//------------------------------------------------------------------------------
	
	private[format] val kind:CParser[String]	= token
	
	private[format] val contentDisposition:CParser[(String,ISeq[(String,String)])]	=
			kind next nextParameter.seq finish LWS
		
	//------------------------------------------------------------------------------
	
	private[format] val major:CParser[String]			= token
	private[format] val minor:CParser[String]			= token
	private[format] val typ:CParser[(String,String)]	= major left symbol('/') next minor
	
	private[format] val contentType:CParser[((String,String),ISeq[(String,String)])]	=
			typ next nextParameter.seq finish LWS
	
	//------------------------------------------------------------------------------
	
	private def oneOrTwo[A,B](it:Either[(A,Option[B]),B]):OneOrTwo[A,B]	=
			it match {
				case Left((a, None))	=> One(a)
				case Left((a, Some(b)))	=> OneAndTwo(a, b)
				case Right(b)			=> Two(b)
			}
			
	private[format] val bytesUnit:CParser[String]						= symbolN("bytes")
	private[format] val bytePos:CParser[Long]							= DIGIT.nes.stringify map { _.toLong } token LWS
	private[format] val byteRangeSpec:CParser[(Long,Option[Long])]		= bytePos left symbol('-') next bytePos.option
	private[format] val suffixByteRangeSpec:CParser[Long]				= symbol('-') right bytePos
	private[format] val byteRangeOne:CParser[OneOrTwo[Long,Long]]		= byteRangeSpec either suffixByteRangeSpec map oneOrTwo
	private[format] val byteRangeSet:CParser[Nes[OneOrTwo[Long,Long]]]	= hash1(byteRangeOne) finish LWS
	
	private[format] val rangeHeader:CParser[Nes[OneOrTwo[Long,Long]]]	=
			bytesUnit right symbol('=') right byteRangeSet
	
	//------------------------------------------------------------------------------
	
	private[format] val cookieName:CParser[String]	= token
	private[format] val cookieOctet:CParser[Char]	=
			cis(0x21)		orElse
			rng(0x23, 0x2b)	orElse
			rng(0x2d, 0x3a)	orElse
			rng(0x3c, 0x5b)	orElse
			rng(0x5d, 0x7e)
	private[format] val cookieValueRaw:CParser[String]				= cookieOctet.seq.stringify
	private[format] val cookieValueQuoted:CParser[String]			= cookieValueRaw inside DQ
	private[format] val cookieValue:CParser[String]					= cookieValueRaw orElse cookieValueQuoted
	private[format] val cookiePair:CParser[(String,String)]			= cookieName left is('=') next cookieValue
	private[format] val cookieString:CParser[ISeq[(String,String)]]	= cookiePair sepSeq (is(';') next SP)
	
	private[format] val cookieHeader:CParser[ISeq[(String,String)]]	= cookieString inside OWS
}
