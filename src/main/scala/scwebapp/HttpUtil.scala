package scwebapp

import java.util.Random
import java.nio.charset.Charset

import scutil.implicits._
import scutil.io.URIComponent

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
				queryString
				.splitAroundChar ('&')
				.map { part =>
					part splitAroundFirstChar '=' match {
						case Some((key, value))	=> (URIComponent decode key, URIComponent decode value)
						case None				=> (URIComponent decode part, "")
					}
				}
				.into (CaseParameters.apply)
			}
}
