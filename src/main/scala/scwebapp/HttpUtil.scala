package scwebapp

import java.util.Random
import java.nio.charset.Charset

import scutil.implicits._
import scutil.io.Charsets

object HttpUtil {
	private val multipartChars	= "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	
	private val random	= new Random
	
	def multipartBoundary():String	=
			random string (
				multipartChars,
				30 + (random nextInt 10)
			)
	
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
}
