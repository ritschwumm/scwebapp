package scwebapp

import java.net._
import java.nio.charset.Charset

object URIComponent {
	def encode(s:String, encoding:Charset):String	=
			URLEncoder
			.encode		(s, encoding.name)
			.replace	("+",	"%20")
			.replace	("%21", "!")
            .replace	("%27", "'")
            .replace	("%28", "(")
            .replace	("%29", ")")
            .replace	("%7E", "~")
		
	def decode(s:String, encoding:Charset):String	=
			URLDecoder decode (
					s replace ("+", "%2b"),
					encoding.name)
}
