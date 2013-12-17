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
	val CTL:CParser[Char]		= rng(0, 31) orElse accept(127)
	val CR:CParser[Char]		= accept('\r')
	val LF:CParser[Char]		= accept('\n')
	val SP:CParser[Char]		= accept(' ')
	val HT:CParser[Char]		= accept('\t')
	val DQ:CParser[Char]		= accept('"')
	
	val CRLF:CParser[String]	= CR follow LF map { case (a, b) => a.toString + b.toString }
	val LWS:CParser[Char]		= CRLF.optional follow (SP orElse HT).many1 map constant(' ')
	val TEXT:CParser[Char]		= LWS orElse (CTL.prevent right OCTET)
	
	/*
	tspecials := 
		"(" / ")" / "<" / ">" / "@" /
		"," / ";" / ":" / "\" / <">
		"/" / "[" / "]" / "?" / "="
	*/
	// NOTE separator ist tspecials und "{} \t"
	
	val separator:CParser[Char]	= in("()<>@,;:\\\"/[]?={} \t")
	val token:CParser[String]	= ((separator orElse CTL).prevent right CHAR).many1 map { _.toSeq mkString "" }
	
	// val ctext:Parser[Char]		= (sat('(') orElse sat(')')).prevent right TEXT
	// val comment:Parser[String]	= '(' ~> (quoted_pair_string | comment | ctext_string).* <~ ')' map { _.mkString }
	
	// TODO check
	val quotedPair:CParser[Char]	=
			accept('\\') right CHAR collect {
				case 'r'	=> '\r'
				case 'n'	=> '\n'
				// case '"'		=> '"'
				// case '\\'	=> '\\'
				case x		=> x
			}
	
	val dqText:CParser[Char]			= DQ.prevent right TEXT
	
	val quotedChar:CParser[Char]		= quotedPair orElse dqText
	val quotedString:CParser[String]	= DQ right quotedChar.many left DQ map { _.mkString }
                     
	//------------------------------------------------------------------------------
		
	def symbol[T](sub:CParser[T])	= mkSymbol(LWS, sub)
	def finish[T](sub:CParser[T])	= mkFinish(LWS, sub)
	
	//------------------------------------------------------------------------------
	
	val attribute:CParser[String]				= symbol(token)
	val value:CParser[String]					= symbol(token orElse quotedString)
	val parameter:CParser[(String,String)]		= attribute left symbol(accept('=')) follow value
	val nextParameter:CParser[(String,String)]	= symbol(accept(';')) right parameter
		
	//------------------------------------------------------------------------------
	
	val kind:CParser[String]	= symbol(token)
	
	val contentDisposition:CParser[(String,Seq[(String,String)])]	= 
			finish(kind follow nextParameter.many)
		
	//------------------------------------------------------------------------------
	
	val major:CParser[String]			= symbol(token)
	val minor:CParser[String]			= symbol(token)
	val typ:CParser[(String,String)]	= major left symbol(accept('/')) follow minor 
	
	val contentType:CParser[((String,String),Seq[(String,String)])]	=
			finish(typ follow nextParameter.many)
}
