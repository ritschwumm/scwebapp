package scwebapp

import java.util.Random
import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._
import scutil.io.Charsets

import scwebapp.parser.string._

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
}
