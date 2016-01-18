package scwebapp
package format

import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._
import scutil.io.Charsets

/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
object HeaderParser {
	def contentLength(headers:Parameters):Tried[String,Option[Long]]	=
			(headers firstString "Content-Length")
			.map { it =>
				HttpParser parseContentLength it toWin so"invalid content length ${it}"
			}
			.sequenceTried
			
	def contentType(headers:Parameters):Tried[String,Option[MimeType]]	=
			(headers firstString "Content-Type")
			.map { it =>
				MimeType parse it toWin so"invalid content type ${it}"
			}
			.sequenceTried
			
	def encoding(headers:Parameters):Tried[String,Option[Charset]]	=
			contentType(headers)
			.map { _ map parseEncoding }
			match {
				case Fail(x)					=> Fail(x)
				case Win(None)					=> Win(None)
				case Win(Some(Fail(x)))			=> Fail(x)
				case Win(Some(Win(None)))		=> Win(None)
				case Win(Some(Win(Some(x))))	=> Win(Some(x))
			}
			
	def parseEncoding(contentType:MimeType):Tried[String,Option[Charset]]	=
			(contentType.parameters firstString "charset")
			.map { it =>
				HttpParser parseEncoding it toWin so"invalid charset ${it}"
			}
			.sequenceTried
			
	// @see http://www.ietf.org/rfc/rfc2617.txt
	def authorizationBasic(headers:Parameters, encoding:Charset):Tried[String,Option[BasicCredentials]]	=
			(headers firstString "Authorization")
			.map	{ it =>
				HttpParser parseBasicAuthentication (it, encoding) toWin so"invalid header ${it}"
			}
			.sequenceTried
			
	def cookies(headers:Parameters):Tried[String,Option[CaseParameters]]	=
			(headers firstString "Cookie")
			.map { it =>
				HttpParser parseCookie it toWin so"invalid cookies header ${it}"
			}
			.sequenceTried
			
	def contentDisposition(headers:Parameters):Tried[String,Option[Disposition]]	=
			(headers firstString "Content-Disposition")
			.map { it =>
				HttpParser parseContentDisposition it toWin so"invalid content disposition ${it}"
			}
			.sequenceTried
	
	// @see https://www.ietf.org/rfc/rfc2047.txt
	def fileName(headers:Parameters):Tried[String,Option[String]]	=
			contentDisposition(headers)
			.map {
				_ map { _.fileName }
			}
			match {
				case Fail(x)	=> Fail(x)
				case Win(x)		=> Win(x.flatten)
			}
}
