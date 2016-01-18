package scwebapp

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

import scutil.lang._
import scutil.implicits._

object HeaderUnparsers {
	// forbidden in name and value: [ ] ( ) = , " / ? @ : ;
	// TODO make sure responses with a cookie are not cached
	def setCookieHeader(
		name:String,
		value:String,
		domain:Option[String]		= None,
		path:Option[String]			= None,	
		comment:Option[String]		= None,
		maxAge:Option[HttpDuration]	= None,	// None deletes on browser exit, zero deletes immediately
		secure:Boolean				= false,
		httpOnly:Boolean			= false
		//version:Int				= 0		// 0=netscape, 1=RFC
	):String = {
		require(name.length != 0, "bad cookie name")
		
		// NOTE if we have a comment or anything is quoted, we need version 1
		// NOTE jetty seems to use "EEE, dd-MMM-yy HH:mm:ss zzz" for an expires header
		
		def alwaysValue(name:String, value:String):Option[ISeq[String]]	=
				Some(Vector(name, value))
		def simpleValue(name:String, value:Option[String]):Option[ISeq[String]]	=
				value	map { it => Vector(name,	quoteMaybe(it)) }
		def simpleFlag(name:String, value:Boolean):Option[ISeq[String]]	=
				value	guard Vector(name)
			
		// NOTE these are not quoted
		val (maxAgeValue, expiresValue)	=
				maxAge
				.map { duration =>
					val expiresDate	=
							if (duration == HttpDuration.zero)	HttpDate.zero
							else								HttpDate.now + duration
					(HttpDurationFormat unparse duration, HttpDateFormat unparse expiresDate)
				}
				.unzip
			
		val values	=
				Vector(
					alwaysValue(quoteMaybe(name),	quoteMaybe(value)),
					alwaysValue("Version",	"1"),
					simpleValue("Path",		path),
					simpleValue("Domain",	domain),
					simpleValue("Expires",	expiresValue),
					simpleValue("Max-Age",	maxAgeValue),
					simpleFlag("Secure",	secure),
					simpleFlag("HttpOnly",	httpOnly),
					simpleValue("Comment",	comment)
				)
				
		values.collapse map { _ mkString "=" } mkString ";"
	}
	
	// NOTE < 20 or > 7f are illegal so it's not a problem when quoteSimple kills line feeds
	private val quoteRequiringChars	= "\",;\\ \t"
	
	private def quoteMaybe(s:String):String	=
			if (quoteNeeded(s))	quoteSimple(s)
			else				s
			
	private def quoteNeeded(s:String):Boolean	=
			s exists { c => quoteRequiringChars contains c }

	//------------------------------------------------------------------------------
	
	// TODO check
	// @see RFC2616
	def quoteSimple(s:String):String	=
			"\"" +
			(
				s flatMap {
					case '"'	=> "\\\""
					case '\\'	=> "\\\\"
					case '\r'	=> " "
					case '\n'	=> " "
					case x		=> x.toString
				}
			) +
			"\""
		
	// used for content-disposition's filename*, @see RFC6266
	// about the encoding, @see RFC5987
	def quoteStar(s:String):String	=
			"UTF-8''" + (s getBytes "UTF-8" map quoteStar1 mkString "")
	
	private def quoteStar1(c:Byte):String	=
			c match {
				case x
				if	x >= 'a' && x <= 'z' ||
					x >= 'A' && x <= 'Z' ||
					x >= '0' && x <= '9'
					=> c.toChar.toString
				
				case '!' | '#' | '$' | '&' | '+' | '-' | '.' | '^' | '_' | '`' | '|' | '~'
					=> c.toChar.toString
					
				case x
					=> "%%%02x" format (c & 0xff)
			}
			
	//------------------------------------------------------------------------------
	
	def formatRange(r:HttpRange):String	=
			so"bytes ${r.start.toString}-${r.end.toString}/${r.total.toString}"
}
