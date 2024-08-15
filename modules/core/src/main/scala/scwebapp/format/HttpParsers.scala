package scwebapp.format

import java.nio.charset.Charset

import scutil.core.implicits.*
import scutil.lang.*
import scutil.codec.*

import scwebapp.data.*
import scparse.ng.text.*

object HttpParsers {
	val ctlChars		= ((0 to 31) :+ 127).map(_.toChar).mkString("")
	val separatorChars	= "()<>@,;:\\\"/[]?={} \t"
	val nonTokenChars	= ctlChars + separatorChars

	//------------------------------------------------------------------------------

	/*
	val CHAR:TextParser[Char]		= rng(0, 127)
	val UPALPHA:TextParser[Char]	= rng('A', 'Z')
	val LOALPHA:TextParser[Char]	= rng('a', 'z')
	val ALPHA:TextParser[Char]		= UPALPHA `orElse` LOALPHA
	val LWS:TextParser[Char]		= CRLF.option.next(WSP.nes).tag(' ')
	*/

	val OCTET:TextParser[Char]	= TextParser.anyIn(0, 255)
	val ALPHA:TextParser[Char]	= TextParser.anyIn('A', 'Z') `orElse` TextParser.anyIn('a', 'z')
	val BIT:TextParser[Char]	= TextParser.is('0') `orElse` TextParser.is('1')
	val CHAR:TextParser[Char]	= TextParser.anyIn(1,127)
	val DIGIT:TextParser[Char]	= TextParser.anyIn('0', '9')
	val HEXDIG:TextParser[Char]	= DIGIT `orElse` TextParser.anyIn('a', 'f') `orElse` TextParser.anyIn('A', 'F')
	val CTL:TextParser[Char]	= TextParser.anyIn(0, 31) `orElse` TextParser.is(127)
	val VCHAR:TextParser[Char]	= TextParser.anyIn(33, 126)
	val CR:TextParser[Char]		= TextParser.is('\r')
	val LF:TextParser[Char]		= TextParser.is('\n')
	val SP:TextParser[Char]		= TextParser.is(' ')
	val HTAB:TextParser[Char]	= TextParser.is('\t')
	val DQUOTE:TextParser[Char]	= TextParser.is('"')

	val CRLF:TextParser[String]	= CR.next(LF).map{ (a, b) => a.toString + b.toString }
	val WSP:TextParser[Char]	= SP `orElse` HTAB
	val LWSP:TextParser[Char]	= CRLF.option.next(WSP).seq.tag(' ')

	val TEXT:TextParser[Char]	= CTL.not.right(OCTET)
	val OWS:TextParser[Unit]	= CRLF.option.next(SP).seq.tag(())

	//------------------------------------------------------------------------------

	def symbol(c:Char):TextParser[Char]			= TextParser.is(c).eatLeft(LWSP)
	def symbolN(s:String):TextParser[String]	= TextParser.isString(s).eatLeft(LWSP)

	// NOTE separator is tspecials and "{} \t" and CTL
	val tokenSeparator:TextParser[Char]	= TextParser.anyOf(nonTokenChars)
	val token:TextParser[String]		= tokenSeparator.not.right(CHAR).nes.stringify.eatLeft(LWSP)

	// val ctext:Parser[Char]		= (TextParser.is('(')  orElse TextParser.is(')')).prevents.right(TEXT)
	// val comment:Parser[String]	= '(' ~> (quoted_pair_string | comment | ctext_string).* <~ ')' stringify

	val quotedPair:TextParser[Char]		= TextParser.is('\\').right(CHAR)
	val dqText:TextParser[Char]			= DQUOTE.not.right(TEXT)
	val quotedChar:TextParser[Char]		= quotedPair `orElse` dqText
	val quotedString:TextParser[String]	= quotedChar.seq.within(DQUOTE).stringify.eatLeft(LWSP)

	val hashSepa:TextParser[Char]	= symbol(',')
	def hash[T](sub:TextParser[T]):TextParser[Seq[T]]	= sub.seqSepBy(hashSepa)
	def hash1[T](sub:TextParser[T]):TextParser[Nes[T]]	= sub.nesSepBy(hashSepa)

	//------------------------------------------------------------------------------

	/*
	val attribute:TextParser[String]					= token
	val value:TextParser[String]						= token `orElse` quotedString
	val parameter:TextParser[(String,String)]			= attribute left symbol('=') next value
	val nextParameter:TextParser[(String,String)]		= symbol(';') right parameter
	val parameterList:TextParser[NoCaseParameters]	= nextParameter.seq.map(NoCaseParameters.apply)
	*/

	// TODO is this allowed for all parameters, or just for content-disposition?
	// NOTE this is _not_ allowed for content-disposition in multipart/form-data

	val attrChar:TextParser[Char]	= ALPHA `orElse` DIGIT `orElse` TextParser.anyOf("!#$&+-.^_`|~")
	val parmName:TextParser[String]	= attrChar.nes.stringify

	val parmValue:TextParser[String]		= token `orElse` quotedString
	val regParameter:TextParser[(Boolean,(String,String))]	=
		parmName.eatLeft(WSP).left(symbol('=')).next(parmValue).map(false -> _)

	// @see https://tools.ietf.org/html/rfc5987

	val hexNibble:TextParser[Int]	=
		HEXDIG.map {
			case x if x >= '0'	&& x <= '9'	=> x - '0' + 0
			case x if x >= 'a'	&& x <= 'f'	=> x - 'a' + 10
			case x if x >= 'A'	&& x <= 'F'	=> x - 'A' + 10
		}
	val hexByte:TextParser[Byte]	=
		hexNibble.next(hexNibble).map { (h, l) =>
			((h << 4) | l).toByte
		}
	val pctEncoded:TextParser[Byte]				= TextParser.is('%').right(hexByte)
	val attrCharByte:TextParser[Byte]			= attrChar.map(_.toByte)
	val valueCharBytes:TextParser[ByteString]	= pctEncoded.orElse( attrCharByte).seq.map(ByteString.fromIterable)

	val mimeCharsetC:TextParser[Char]		= ALPHA `orElse` DIGIT `orElse` TextParser.anyOf("!#$%&+-^_`{}~")
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
		simpleCharset.map(Some.apply)	`orElse`
		mimeCharset.tag(None)

	// TODO hack
	val language:TextParser[String]	= extValuePart

	val extValueOpt:TextParser[Option[String]]	=
		for {
			charset		<- charset
			_			<- TextParser.is('\'')
			language	<- language
			_			<- TextParser.is('\'')
			bytes		<- valueCharBytes
		}
		yield {
			charset.flatMap{ it => it.decodeEitherByteString(bytes).toOption }
		}

	val extValue:TextParser[String]	=
		extValueOpt.eatLeft(LWSP).flattenOptionNamed("extValue")

	val extParName:TextParser[String]	= parmName.left(TextParser.is('*'))

	val extParameter:TextParser[(Boolean,(String,String))]	=
		extParName.eatLeft(WSP).left(symbol('=')).next(extValue).map(true -> _)

	val parameter:TextParser[(Boolean,(String,String))]			= regParameter.orElse(extParameter).eatLeft(LWSP)
	val nextParameter:TextParser[(Boolean,(String,String))]		= symbol(';').right(parameter)
	val manyParameters:TextParser[Seq[(Boolean,(String,String))]]	= nextParameter.seq

	// moves extended parameters to the front
	def extendedFirst(it:Seq[(Boolean,(String,String))]):Seq[(String,String)]	=
		it.collect { case (true,	kv) => kv }	++
		it.collect { case (false,	kv) => kv }

	val parameterList:TextParser[NoCaseParameters]	=
		manyParameters.map { list => NoCaseParameters(extendedFirst(list)) }

	//------------------------------------------------------------------------------

	/*
	// TODO has been copied into two other places to avoid a cyclic dependency from HttpParsers to QValue and back
	val qParam:TextParser[QValue]	=
		symbol('q') right symbol('=') right (QValue.parser eatLeft LWSP)
	*/

	//------------------------------------------------------------------------------

	val longZero:TextParser[Long]	= TextParser.is('0').tag(0L)
	val longPositive:TextParser[Long]	=
		TextParser.anyIn('1', '9').next(DIGIT.seq).map { (h, t)	=>
			((h +: t) foldLeft 0L) { (o, d) =>
				o * 10 + (d - '0')
			}
		}
	val longUnsigned:TextParser[Long]	= longZero `orElse` longPositive

	//------------------------------------------------------------------------------

	val base64Char:TextParser[Char]	=
		ALPHA `orElse` DIGIT `orElse` TextParser.anyOf("+/=")

	def base64(charset:Charset):TextParser[String]	=
		base64Char.seq.stringify.mapFilter(Base64.decodeByteString).named("Base64").mapFilter { it => charset.decodeEitherByteString(it).toOption }.named(s"String[${charset.name}]")

	//------------------------------------------------------------------------------

	val dateValue:TextParser[HttpDate]	=
		TextParser.anyIn(32, 126).seq.stringify.map(_.trim).mapFilter(HttpDate.parse).named("HttpDate")
}
