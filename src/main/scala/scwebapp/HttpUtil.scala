package scwebapp

import java.util.Random
import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._

object HttpUtil {
	private val multipartChars	= "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray
	
	def multipartBoundary():String	= {
		val random	= new Random
		val size	= 30 + (random nextInt 10)
		0 until size map { _ => multipartChars(random nextInt multipartChars.length) } mkString ""
	}
	
	// @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html
	def quoteString(s:String):String	=
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
			
	def getCharset(contentType:MimeType):Tried[String,Option[Charset]]	=
			(contentType.parameters firstString "charset")
			.map { name => charsetByName(name) mapFail constant(name) }
			.sequenceTried
			
	// TODO lib scutil 0.38.0
	def charsetByName(name:String):Tried[IllegalArgumentException,Charset]	=
			Tried.catchSpecific[IllegalArgumentException,Charset](Charset forName name)
}
