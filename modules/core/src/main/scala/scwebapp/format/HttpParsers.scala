package scwebapp.format

import java.nio.charset.Charset

import scutil.base.implicits._
import scutil.lang._
import scutil.codec._

import scwebapp.data._
import scparse.ng.text._

object HttpParsers {
	val ctlChars		= ((0 to 31) :+ 127) map { _.toChar } mkString ""
	val separatorChars	= "()<>@,;:\\\"/[]?={} \t"
	val nonTokenChars	= ctlChars + separatorChars

	//------------------------------------------------------------------------------

	/*
	val CHAR:TextParser[Char]		= rng(0, 127)
	val UPALPHA:TextParser[Char]	= rng('A', 'Z')
	val LOALPHA:TextParser[Char]	= rng('a', 'z')
	val ALPHA:TextParser[Char]	= UPALPHA orElse LOALPHA
	val LWS:TextParser[Char]		= CRLF.option next WSP.nes tag ' '
	*/

	val OCTET:TextParser[Char]	= TextParser anyCharInRange (0, 255)
	val ALPHA:TextParser[Char]	= TextParser.anyCharInRange('A', 'Z') orElse TextParser.anyCharInRange('a', 'z')
	val BIT:TextParser[Char]	= TextParser.isChar('0') orElse TextParser.isChar('1')
	val CHAR:TextParser[Char]	= TextParser anyCharInRange (1,127)
	val DIGIT:TextParser[Char]	= TextParser anyCharInRange ('0', '9')
	val HEXDIG:TextParser[Char]	= DIGIT orElse TextParser.anyCharInRange('a', 'f') orElse TextParser.anyCharInRange('A', 'F')
	val CTL:TextParser[Char]	= TextParser.anyCharInRange(0, 31) orElse TextParser.isChar(127)
	val VCHAR:TextParser[Char]	= TextParser anyCharInRange (33, 126)
	val CR:TextParser[Char]		= TextParser isChar '\r'
	val LF:TextParser[Char]		= TextParser isChar '\n'
	val SP:TextParser[Char]		= TextParser isChar ' '
	val HTAB:TextParser[Char]	= TextParser isChar '\t'
	val DQUOTE:TextParser[Char]	= TextParser isChar '"'

	val CRLF:TextParser[String]	= CR next LF map { case (a, b) => a.toString + b.toString }
	val WSP:TextParser[Char]		= SP orElse HTAB
	val LWSP:TextParser[Char]		= (CRLF.option next WSP).seq tag ' '

	val TEXT:TextParser[Char]		= CTL.prevents right OCTET
	val OWS:TextParser[Unit]		= (CRLF.option next WSP).seq tag (())

	//------------------------------------------------------------------------------

	def symbol(c:Char):TextParser[Char]			= TextParser isChar		c	eatLeft LWSP
	def symbolN(s:String):TextParser[String]	= TextParser isString	s	eatLeft LWSP

	// NOTE separator is tspecials and "{} \t" and CTL
	val tokenSeparator:TextParser[Char]	= TextParser anyCharOf nonTokenChars
	val token:TextParser[String]		= (tokenSeparator.prevents right CHAR).nes.stringify eatLeft LWSP

	// val ctext:Parser[Char]		= (sat('(') orElse sat(')')).prevents right TEXT
	// val comment:Parser[String]	= '(' ~> (quoted_pair_string | comment | ctext_string).* <~ ')' stringify

	val quotedPair:TextParser[Char]		= TextParser.isChar('\\') right CHAR
	val dqText:TextParser[Char]			= DQUOTE.prevents right TEXT
	val quotedChar:TextParser[Char]		= quotedPair orElse dqText
	val quotedString:TextParser[String]	= (quotedChar.seq within DQUOTE).stringify eatLeft LWSP

	val hashSepa:TextParser[Char]	= symbol(',')
	def hash[T](sub:TextParser[T]):TextParser[Seq[T]]	= sub sepSeq	hashSepa
	def hash1[T](sub:TextParser[T]):TextParser[Nes[T]]	= sub sepNes	hashSepa

	//------------------------------------------------------------------------------

	/*
	val attribute:TextParser[String]					= token
	val value:TextParser[String]						= token orElse quotedString
	val parameter:TextParser[(String,String)]			= attribute left symbol('=') next value
	val nextParameter:TextParser[(String,String)]		= symbol(';') right parameter
	val parameterList:TextParser[NoCaseParameters]	= nextParameter.seq map NoCaseParameters.apply
	*/

	// TODO is this allowed for all parameters, or just for content-disposition?
	// NOTE this is _not_ allowed for content-disposition in multipart/form-data

	val attrChar:TextParser[Char]	= ALPHA orElse DIGIT orElse TextParser.anyCharOf("!#$&+-.^_`|~")
	val parmName:TextParser[String]	= attrChar.nes.stringify

	val parmValue:TextParser[String]		= token orElse quotedString
	val regParameter:TextParser[(Boolean,(String,String))]	=
		parmName eatLeft WSP left symbol('=') next parmValue map { false -> _ }

	// @see https://tools.ietf.org/html/rfc5987

	val hexNibble:TextParser[Int]	=
		HEXDIG map {
			case x if x >= '0'	&& x <= '9'	=> x - '0' + 0
			case x if x >= 'a'	&& x <= 'f'	=> x - 'a' + 10
			case x if x >= 'A'	&& x <= 'F'	=> x - 'A' + 10
		}
	val hexByte:TextParser[Byte]	=
		(hexNibble next hexNibble) map { case (h, l) =>
			((h << 4) | l).toByte
		}
	val pctEncoded:TextParser[Byte]				= TextParser.isChar('%') right hexByte
	val attrCharByte:TextParser[Byte]			= attrChar map { _.toByte }
	val valueCharBytes:TextParser[ByteString]	= (pctEncoded orElse attrCharByte).seq map ByteString.fromSeq

	val mimeCharsetC:TextParser[Char]		= ALPHA orElse DIGIT orElse TextParser.anyCharOf("!#$%&+-^_`{}~")
	val mimeCharset:TextParser[String]		= mimeCharsetC.nes.stringify

	// TODO hack
	val extValuePart:TextParser[String]		= mimeCharsetC.seq.stringify

	// NOTE the rfc grammar requires uppercase, but the examples do not
	val simpleCharset:TextParser[Charset]	=
		extValuePart
		.map	(CaseUtil.lowerCase)
		.collect {
			case "utf-8"		=> Charsets.utf_8
			case "iso-8859-1"	=> Charsets.iso_8859_1
		}
		.named ("Charset")

	// NOTE this ignores a mimeCharset
	val charset:TextParser[Option[Charset]]	=
		(simpleCharset	map Some.apply)	orElse
		(mimeCharset	tag None)

	// TODO hack
	val language:TextParser[String]	= extValuePart

	val extValueOpt:TextParser[Option[String]]	=
		for {
			charset		<- charset
			_			<- TextParser isChar '\''
			language	<- language
			_			<- TextParser isChar '\''
			bytes		<- valueCharBytes
		}
		yield {
			charset flatMap { it => (it decodeEitherByteString bytes).toOption }
		}

	val extValue:TextParser[String]	=
		extValueOpt eatLeft LWSP collapseNamed "extValue"

	val extParName:TextParser[String]	= parmName left TextParser.isChar('*')

	val extParameter:TextParser[(Boolean,(String,String))]	=
		extParName eatLeft WSP left symbol('=') next extValue map { true -> _ }

	val parameter:TextParser[(Boolean,(String,String))]			= regParameter orElse extParameter eatLeft LWSP
	val nextParameter:TextParser[(Boolean,(String,String))]		= symbol(';') right parameter
	val manyParameters:TextParser[Seq[(Boolean,(String,String))]]	= nextParameter.seq

	// moves extended parameters to the front
	def extendedFirst(it:Seq[(Boolean,(String,String))]):Seq[(String,String)]	=
		(it collect { case (true,	kv) => kv })	++
		(it collect { case (false,	kv) => kv })

	val parameterList:TextParser[NoCaseParameters]	=
		manyParameters map { list => NoCaseParameters(extendedFirst(list)) }

	//------------------------------------------------------------------------------

	val qParam:TextParser[QValue]	=
		symbol('q') right symbol('=') right (QValue.parser eatLeft LWSP)

	//------------------------------------------------------------------------------

	val longZero:TextParser[Long]	= TextParser.isChar('0') tag 0L
	val longPositive:TextParser[Long]	=
		TextParser.anyCharInRange('1', '9') next DIGIT.seq map { case (h, t)	=>
			((h +: t) foldLeft 0L) { (o, d) =>
				o * 10 + (d - '0')
			}
		}
	val longUnsigned:TextParser[Long]	= longZero orElse longPositive

	//------------------------------------------------------------------------------

	val base64Char:TextParser[Char]	=
		ALPHA orElse DIGIT orElse TextParser.anyCharOf("+/=")

	def base64(charset:Charset):TextParser[String]	=
		base64Char.seq.stringify collapseMap Base64.decodeByteString named "Base64" collapseMap { it => (charset decodeEitherByteString it).toOption } named s"String[${charset.name}]"

	//------------------------------------------------------------------------------

	val dateValue:TextParser[HttpDate]	=
		TextParser.anyCharInRange(32, 126).seq.stringify map { _.trim } collapseMap HttpDate.parse named "HttpDate"
}
