package scwebapp

import java.util.Random
import java.nio.charset.Charset

import scutil.implicits._

object HttpUtil {
	private val multipartChars	= "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	
	private val random	= new Random
	
	def multipartBoundary():String	=
			random string (
				multipartChars,
				30 + (random nextInt 10)
			)
			
	def parseQueryParameters(queryString:String, encoding:Charset):CaseParameters	=
			if (queryString.isEmpty)	CaseParameters.empty
			else {
				def decode(s:String):String	= UrlCodec decode (encoding, s)
				queryString
				.splitAroundChar ('&')
				.map { part =>
					part splitAroundFirstChar '=' match {
						case Some((key, value))	=> (decode(key), decode(value))
						case None				=> (decode(part), "")
					}
				}
				.into (CaseParameters.apply)
			}
}
