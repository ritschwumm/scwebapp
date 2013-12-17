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
	
	//------------------------------------------------------------------------------
	
	// TODO check
	// @see http://www.ietf.org/rfc/rfc2616.txt
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
			
	/*
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
	*/
	
	//------------------------------------------------------------------------------
	
	import scwebapp.parser.string._
	
	def parseContentDisposition(it:String):Option[(String,NoCaseParameters)]	=
			HttpParser.contentDisposition parseStringOption it map { case (kind, params) => (kind, NoCaseParameters(params)) }
		
	def parseContentType(it:String):Option[((String,String),NoCaseParameters)]	=
			HttpParser.contentType parseStringOption it map { case (kind, params) => (kind, NoCaseParameters(params)) }
		
	def parseCookie(it:String):Option[CaseParameters]	=
			HttpParser.cookieHeader parseStringOption it map CaseParameters.apply
}
