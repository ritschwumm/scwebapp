package scwebapp.format

import java.nio.charset.Charset

import scutil.base.implicits._
import scutil.lang._
import scutil.codec._

import scwebapp.data._
import scwebapp.parser.Parser._
import scwebapp.parser.string._

object HttpParsers {
	val ctlChars		= ((0 to 31) :+ 127) map { _.toChar } mkString ""
	val separatorChars	= "()<>@,;:\\\"/[]?={} \t"
	val nonTokenChars	= ctlChars + separatorChars
	
	//------------------------------------------------------------------------------
	
	/*
	val CHAR:CParser[Char]		= rng(0, 127)
	val UPALPHA:CParser[Char]	= rng('A', 'Z')
	val LOALPHA:CParser[Char]	= rng('a', 'z')
	val ALPHA:CParser[Char]		= UPALPHA orElse LOALPHA
	val LWS:CParser[Char]		= CRLF.option next WSP.nes tag ' '
	*/
	
	val OCTET:CParser[Char]		= rng(0, 255)
	val ALPHA:CParser[Char]		= rng('A', 'Z') orElse rng('a', 'z')
	val BIT:CParser[Char]		= is('0') orElse is('1')
	val CHAR:CParser[Char]		= rng(1,127)
	val DIGIT:CParser[Char]		= rng('0', '9')
	val HEXDIG:CParser[Char]	= DIGIT orElse rng('a', 'f') orElse rng('A', 'F')
	val CTL:CParser[Char]		= rng(0, 31) orElse is(127)
	val VCHAR:CParser[Char]		= rng(33, 126)
	val CR:CParser[Char]		= is('\r')
	val LF:CParser[Char]		= is('\n')
	val SP:CParser[Char]		= is(' ')
	val HTAB:CParser[Char]		= is('\t')
	val DQUOTE:CParser[Char]	= is('"')
	
	val CRLF:CParser[String]	= CR next LF map { case (a, b) => a.toString + b.toString }
	val WSP:CParser[Char]		= SP orElse HTAB
	val LWSP:CParser[Char]		= (CRLF.option next WSP).seq tag ' '
	
	val TEXT:CParser[Char]		= CTL.prevent right OCTET
	val OWS:CParser[Unit]		= (CRLF.option next WSP).seq tag (())
	
	//------------------------------------------------------------------------------
	
	def symbol(c:Char):CParser[Char]		= is(c)		eating LWSP
	def symbolN(s:String):CParser[String]	= sis(s)	eating LWSP
	
	// NOTE separator is tspecials and "{} \t" and CTL
	val tokenSeparator:CParser[Char]	= in(nonTokenChars)
	val token:CParser[String]			= (tokenSeparator.prevent right CHAR).nes.stringify eating LWSP
	
	// val ctext:Parser[Char]		= (sat('(') orElse sat(')')).prevent right TEXT
	// val comment:Parser[String]	= '(' ~> (quoted_pair_string | comment | ctext_string).* <~ ')' stringify
	
	val quotedPair:CParser[Char]		= is('\\') right CHAR
	val dqText:CParser[Char]			= DQUOTE.prevent right TEXT
	val quotedChar:CParser[Char]		= quotedPair orElse dqText
	val quotedString:CParser[String]	= (quotedChar.seq inside DQUOTE).stringify eating LWSP

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
	val parmName:CParser[String]	= attrChar.nes.stringify
	
	val parmValue:CParser[String]		= token orElse quotedString
	val regParameter:CParser[(Boolean,(String,String))]	=
			parmName eating WSP left symbol('=') next parmValue map { false -> _ }
	
	// @see https://tools.ietf.org/html/rfc5987
	
	val hexNibble:CParser[Int]	=
			HEXDIG map {
				case x if x >= '0'	&& x <= '9'	=> x - '0' + 0
				case x if x >= 'a'	&& x <= 'f'	=> x - 'a' + 10
				case x if x >= 'A'	&& x <= 'F'	=> x - 'A' + 10
			}
	val hexByte:CParser[Byte]	=
			(hexNibble next hexNibble) map { case (h, l) =>
				((h << 4) | l).toByte
			}
	val pctEncoded:CParser[Byte]			= is('%') right hexByte
	val attrCharByte:CParser[Byte]			= attrChar map { _.toByte }
	val valueCharBytes:CParser[ByteString]	= (pctEncoded orElse attrCharByte).seq map ByteString.fromISeq
	
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
				charset flatMap { it => (it decodeEitherByteString bytes).toOption }
			}
	
	val extValue:CParser[String]	= (extValueOpt eating LWSP).filterSome
			
	val extParName:CParser[String]	= parmName left cis('*')
		
	val extParameter:CParser[(Boolean,(String,String))]	=
			extParName eating WSP left symbol('=') next extValue map { true -> _ }
	
	val parameter:CParser[(Boolean,(String,String))]			= (regParameter orElse extParameter) eating LWSP
	val nextParameter:CParser[(Boolean,(String,String))]		= symbol(';') right parameter
	val manyParameters:CParser[ISeq[(Boolean,(String,String))]]	= nextParameter.seq
	
	// moves extended parameters to the front
	def extendedFirst(it:ISeq[(Boolean,(String,String))]):ISeq[(String,String)]	=
			(it collect { case (true,	kv) => kv })	++
			(it collect { case (false,	kv) => kv })
			
	val parameterList:CParser[NoCaseParameters]	=
			manyParameters map { list => NoCaseParameters(extendedFirst(list)) }
	
	//------------------------------------------------------------------------------
		
	val qParam:CParser[QValue]	=
			symbol('q') right symbol('=') right (QValue.parser eating LWSP)
		
	//------------------------------------------------------------------------------

	val longZero:CParser[Long]	= cis('0') tag 0L
	val longPositive:CParser[Long]	=
			rng('1', '9') next DIGIT.seq map { case (h, t)	=>
				((h +: t) foldLeft 0L) { (o, d) =>
					o * 10 + (d - '0')
				}
			}
	val longUnsigned:CParser[Long]	= longZero orElse longPositive
		
	//------------------------------------------------------------------------------
	
	val base64Char:CParser[Char]	=
			ALPHA orElse DIGIT orElse in("+/=")
			
	def base64(charset:Charset):CParser[String]	=
			(base64Char).seq.stringify filterMap Base64.decodeByteString filterMap { it => (charset decodeEitherByteString it).toOption }
		
	//------------------------------------------------------------------------------
	
	val dateValue:CParser[HttpDate]	=
			rng(32, 126).seq.stringify map { _.trim } filterMap HttpDate.parse
}
