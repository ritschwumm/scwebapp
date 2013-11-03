package scwebapp

import java.net._
import java.nio.charset.Charset

import scutil.io.Charsets.utf_8

object URIComponent {
	val charset	= utf_8
	
	//------------------------------------------------------------------------------
	//## encoding
	
	/*
	def encode(s:String, encoding:Charset):String	=
			URLEncoder
			.encode		(s, charset.name)
			.replace	("+",	"%20")
			.replace	("%21", "!")
			.replace	("%27", "'")
			.replace	("%28", "(")
			.replace	("%29", ")")
			.replace	("%7E", "~")
	*/
	
	/** percent-escapes everything except alphabetic, decimal digits, - _ . ! ~ * ' ( ) */
	def encode(s:String):String = {
		val bytes	= s getBytes charset
		val out		= new StringBuilder
		var i	= 0
		while (i < bytes.length) {
			val byte	= bytes(i)
			if (passThrough(byte)) {
				out append byte.toChar
			}
			else {
				out append '%'
				out append encodeNibble((byte >> 4) & 0xf).toChar
				out append encodeNibble((byte >> 0) & 0xf).toChar
			}
			i	+= 1
		}
		out.toString
	}
	
	@inline
	private def encodeNibble(nibble:Int):Int =
			if (nibble < 10)	(nibble + '0'		)
			else				(nibble + 'A' - 10	)
	
	@inline 
	private def passThrough(byte:Int):Boolean =
			byte >= 'a' && byte <= 'z'	||
			byte >= 'A' && byte <= 'Z'	||
			byte >= '0' && byte <= '9'	||
			byte == '-' ||
			byte == '_' ||
			byte == '.' ||
			byte == '!' ||
			byte == '~' ||
			byte == '*' ||
			byte == '(' ||
			byte == ')' ||
			byte == '\''
	
	//------------------------------------------------------------------------------
	//## decoding
	
	def decode(s:String):String =
			URLDecoder decode (
					s replace ("+", "%2B"),
					charset.name)
}
