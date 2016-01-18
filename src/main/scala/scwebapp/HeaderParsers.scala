package scwebapp

import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._
import scutil.io.Charsets
import scutil.io.Base64

/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
object HeaderParsers {
	def contentLength(headers:Parameters):Tried[String,Option[Long]]	=
			(headers firstString "Content-Length")
			.map { it =>
				it guardBy { _ matches "\\d+" } flatMap { _.toLongOption } toWin so"invalid content length ${it}"
			}
			.sequenceTried
			
	def contentType(headers:Parameters):Tried[String,Option[MimeType]]	=
			(headers firstString "Content-Type")
			.map { it =>
				MimeType parse it toWin so"invalid content type ${it}"
			}
			.sequenceTried
			
	def encoding(headers:Parameters):Tried[String,Option[Charset]]	=
			contentType(headers) map { _ map parseEncoding } match {
				case Fail(x)					=> Fail(x)
				case Win(None)					=> Win(None)
				case Win(Some(Fail(x)))			=> Fail(x)
				case Win(Some(Win(None)))		=> Win(None)
				case Win(Some(Win(Some(x))))	=> Win(Some(x))
			}
			
	private def parseEncoding(contentType:MimeType):Tried[String,Option[Charset]]	=
			(contentType.parameters firstString "charset")
			.map { it =>
				Charsets byName it mapFail constant(so"invalid charset ${it}")
			}
			.sequenceTried
			
	// @see http://www.ietf.org/rfc/rfc2617.txt
	def authorizationBasic(headers:Parameters, encoding:Charset):Tried[String,Option[BasicAuthentication]]	=
			(headers firstString "Authorization")
			.map	{ parseBasic(_, encoding) }
			.sequenceTried
			
	private def parseBasic(header:String, encoding:Charset):Tried[String,BasicAuthentication]	=
			for {
				code	<- header cutPrefix "Basic "						toWin	so"missing Basic prefix in ${header}"
				bytes	<- Base64 decode code								toWin	so"invalid base64 code in ${code}"
				str		<- Catch.exception in (new String(bytes, encoding))	mapFail	constant("invalid string bytes")
				pair	<- str splitAroundFirstChar ':'						toWin	so"missing colon separator in ${str}"
			}
			yield BasicAuthentication tupled pair
			
	def cookies(headers:Parameters):CaseParameters	=
			(headers firstString "Cookie")
			.flatMap	(HttpParser.parseCookie)
			.getOrElse	(CaseParameters.empty)
			
	def contentDisposition(headers:Parameters):Option[String]	=
			headers firstString "Content-Disposition"
	
	// @see https://www.ietf.org/rfc/rfc2047.txt
	def fileName(headers:Parameters):Tried[String,Option[String]]	=
			contentDisposition(headers)
			.map { it:String =>
				(HttpParser parseContentDisposition it)
				.flatMap	{ _._2 firstString "filename" }
				.toWin		(so"invalid content disposition ${it}")
			}
			.sequenceTried
}
