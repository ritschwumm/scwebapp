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
			
	def cookies(headers:Parameters):Tried[String,Option[CaseParameters]]	=
			(headers firstString "Cookie")
			.map { it =>
				HttpParser parseCookie it toWin so"invalid cookies header ${it}"
			}
			.sequenceTried
			
	def contentDisposition(headers:Parameters):Tried[String,Option[ContentDisposition]]	=
			(headers firstString "Content-Disposition")
			.map { it =>
				HttpParser parseContentDisposition it toWin so"invalid content disposition ${it}"
			}
			.sequenceTried
	
	// @see https://www.ietf.org/rfc/rfc2047.txt
	def fileName(headers:Parameters):Tried[String,Option[String]]	=
			contentDisposition(headers) map { _ map { _.parameters firstString "filename" } } match {
				case Fail(x)			=> Fail(x)
				case Win(None)			=> Win(None)
				case Win(Some(None))	=> Win(None)
				case Win(Some(Some(x)))	=> Win(Some(x))
			}
			
	def parseRangeHeader(total:Long)(rangeHeader:String):Option[ISeq[HttpRange]]	= {
		import scwebapp.parser.string._
		
		val last	= total - 1
		
		def mkRange(it:Either[(Long,Option[Long]),Long]):Option[HttpRange]	=
				it matchOption {
					case Left((start, Some(end)))	if start <= (end min last)		=> HttpRange(start,			end min last,	total)
					case Left((start, None))		if start <= last				=> HttpRange(start,			last,			total)
					case Right(count)				if count > 0 && count <= total	=> HttpRange(total - count,	last,			total)
				}
		
		(HttpParser.rangeHeader parseStringOption rangeHeader)
		.map	{ _.toVector flatMap mkRange }
		.filter	{ _.nonEmpty }
	}
}
