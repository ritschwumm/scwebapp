package scwebapp

import java.util.Random
import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._
import scutil.io.Charsets

object HttpUtil {
	private val multipartChars	= "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray
	
	def multipartBoundary():String	= {
		val random	= new Random
		val size	= 30 + (random nextInt 10)
		0 until size map { _ => multipartChars(random nextInt multipartChars.length) } mkString ""
	}
	
	// @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html
	def quote(s:String):String	=
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
			
	// @see http://tools.ietf.org/html/rfc2184 for non-ascii
	def unquote(s:String):String	= {
		if (s.length >= 2 && (s startsWith "\"") && (s endsWith "\"")) {
			val b	= new StringBuilder
			var i	= 1
			while (i < s.length-1) {
				s charAt i match {
					case '\\'	=>
						// TODO not out of bounds, but still fishy
						i += 1
						s charAt i match {
							case '"'	=> b append '"'
							case '\\'	=> b append '\\'
							case 'r'	=> b append '\r'
							case 'n'	=> b append '\n'
							case x		=> b append '\\'; b append x
						}
					case x		=> b append x
				}
				i	+= 1
			}
			b.toString
		}
		else s
	}
			
	def getCharset(contentType:MimeType):Tried[String,Option[Charset]]	=
			(contentType.parameters firstString "charset")
			.map { name => Charsets byName name mapFail constant(name) }
			.sequenceTried
}
