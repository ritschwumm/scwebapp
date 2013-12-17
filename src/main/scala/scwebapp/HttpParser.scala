package scwebapp

import scutil.lang._

import scwebapp.parser._
import scwebapp.parser.string._

/*
@see http://www.ietf.org/rfc/rfc2045.txt
@see http://www.ietf.org/rfc/rfc2046.txt
@see http://www.ietf.org/rfc/rfc2047.txt
@see http://www.ietf.org/rfc/rfc2183.txt
@see http://www.ietf.org/rfc/rfc2184.txt for non-ascii
@see http://www.ietf.org/rfc/rfc2616.txt
*/
	
object HttpParser {
	import Parser._
		
	//------------------------------------------------------------------------------
	
	val OCTET:CParser[Char]		= rng(0, 255)
	val CHAR:CParser[Char]		= rng(0, 127)
	val UPALPHA:CParser[Char]	= rng('A', 'Z')
	val LOALPHA:CParser[Char]	= rng('a', 'z')
	val ALPHA:CParser[Char]		= UPALPHA orElse LOALPHA
	val DIGIT:CParser[Char]		= rng('0', '9')
	val HEX:CParser[Char]		= DIGIT orElse rng('a', 'f') orElse rng('A', 'F')
	val CTL:CParser[Char]		= rng(0, 31) orElse is(127)
	val CR:CParser[Char]		= is('\r')
	val LF:CParser[Char]		= is('\n')
	val SP:CParser[Char]		= is(' ')
	val HT:CParser[Char]		= is('\t')
	val DQ:CParser[Char]		= is('"')
	
	val CRLF:CParser[String]	= CR next LF map { case (a, b) => a.toString + b.toString }
	val LWS:CParser[Char]		= CRLF.option next (SP orElse HT).nes tag ' '
	val TEXT:CParser[Char]		= LWS orElse (CTL.prevent right OCTET)
	
	def symbol(c:Char):CParser[Char]	= is(c) token LWS
	
	// NOTE separator ist tspecials und "{} \t"
	val separator:CParser[Char]	= in("()<>@,;:\\\"/[]?={} \t")
	val token:CParser[String]	= ((separator orElse CTL).prevent right CHAR).nes map { _.toSeq mkString "" } token LWS
	
	// val ctext:Parser[Char]		= (sat('(') orElse sat(')')).prevent right TEXT
	// val comment:Parser[String]	= '(' ~> (quoted_pair_string | comment | ctext_string).* <~ ')' map { _.mkString }
	
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
	val quotedString:CParser[String]	= DQ right quotedChar.seq left DQ map { _.mkString } token LWS
                     
	//------------------------------------------------------------------------------
		
	val attribute:CParser[String]				= token
	val value:CParser[String]					= token orElse quotedString
	val parameter:CParser[(String,String)]		= attribute left symbol('=') next value
	val nextParameter:CParser[(String,String)]	= symbol(';') right parameter
		
	//------------------------------------------------------------------------------
	
	val kind:CParser[String]	= token
	
	val contentDisposition:CParser[(String,Seq[(String,String)])]	= 
			kind next nextParameter.seq finish LWS
		
	//------------------------------------------------------------------------------
	
	val major:CParser[String]			= token
	val minor:CParser[String]			= token
	val typ:CParser[(String,String)]	= major left symbol('/') next minor 
	
	val contentType:CParser[((String,String),Seq[(String,String)])]	=
			typ next nextParameter.seq finish LWS
	
	//------------------------------------------------------------------------------
	
	val bytePos:CParser[Long]						= DIGIT.nes map { _.toVector.mkString.toLong } token LWS
	val byteRangeSpec:CParser[(Long,Option[Long])]	= bytePos left symbol('-') next bytePos.option
	val suffixByteRangeSpec:CParser[Long]			= symbol('-') right bytePos
	val byteRangeOne:CParser[Either[(Long,Option[Long]),Long]]		= byteRangeSpec either suffixByteRangeSpec
	
	val byteRangeSet:CParser[Nes[Either[(Long,Option[Long]),Long]]]	= byteRangeOne sepNes symbol(',') finish LWS
}
