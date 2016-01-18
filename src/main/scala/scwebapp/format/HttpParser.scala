package scwebapp.format

import java.nio.charset.Charset

import scutil.lang._
import scutil.io.Charsets

import scwebapp.data._
import scwebapp.parser.string._

object HttpParser {
	def parseContentLength(it:String):Option[Long]	=
			HttpParsers.contentLength parseStringOption it
		
	def parseContentDisposition(it:String):Option[Disposition]	=
			HttpParsers.contentDisposition parseStringOption it
		
	def parseContentType(it:String):Option[MimeType]	=
			HttpParsers.contentType parseStringOption it
		
	def parseCookie(it:String):Option[CaseParameters]	=
			HttpParsers.cookieHeader parseStringOption it
			
	def parseEncoding(name:String):Option[Charset]	=
			(Charsets byName name).toOption
		
	def parseBasicAuthentication(header:String, encoding:Charset):Option[BasicCredentials]	=
			(HttpParsers basicAuthentication encoding) parseStringOption header
			
	def parseRangeHeader(total:Long)(value:String):Option[ISeq[RequestRange]]	=
			(HttpParsers.rangeHeader parseStringOption value) map { nes =>
				nes.toVector flatMap (RequestRange parse total)
			}
}
