package scwebapp
package format

import org.specs2.mutable._

import scutil.lang.Nes
import scutil.io.Charsets

import scwebapp.data._
import scwebapp.parser.string._

class HttpParsersTest extends Specification {
	"star parameters" should {
		"parse a star parameter name" in {
			HttpParsers.extParName parseStringOption "filename*" mustEqual Some("filename")
		}
		"parse a star parameter charset" in {
			HttpParsers.charset parseStringOption "UTF-8" mustEqual Some(Some(Charsets.utf_8))
		}
		"parse a star parameter language" in {
			HttpParsers.language parseStringOption "" mustEqual Some("")
		}
		"parse a star parameter's bytes" in {
			HttpParsers.valueCharBytes parseStringOption "t%c3%a4st" map { _.toVector } mustEqual Some(("täst" getBytes "utf-8").toVector)
		}
		"parse a star parameter value" in {
			HttpParsers.extValue parseStringOption "UTF-8''t%c3%a4st" mustEqual Some("täst")
		}
		"parse a star parameter value" in {
			HttpParsers.extValue parseStringOption "ISO-8859-1''t%e4st" mustEqual Some("täst")
		}
		"parse a star parameter" in {
			HttpParsers.extParameter parseStringOption "filename*=UTF-8''t%c3%a4st" mustEqual Some((true, ("filename", "täst")))
		}
	}
	
	"basic parser should" should {
		"parse an empty quoted string" in {
			HttpParsers.quotedString parseStringOption "\"\"" mustEqual Some("")
		}
		"parse an OCTET" in {
			HttpParsers.OCTET parseStringOption "t" mustEqual Some('t')
		}
		"parse a TEXT" in {
			HttpParsers.TEXT parseStringOption "t" mustEqual Some('t')
		}
		"parse a dqText" in {
			HttpParsers.dqText parseStringOption "t" mustEqual Some('t')
		}
		"parse a quotedChar" in {
			HttpParsers.quotedChar parseStringOption "t" mustEqual Some('t')
		}
		"parse a quotedString" in {
			HttpParsers.quotedString parseStringOption "\"test\"" mustEqual Some("test")
		}
	}
	
	"parsing content disposition headers" should {
		"work without parameters" in {
			HttpParsers.contentDisposition parseStringOption "attachment" mustEqual Some(Disposition(DispositionAttachment,  None))
		}
		"work with a token parameter" in {
			HttpParsers.contentDisposition parseStringOption "attachment; filename=test" mustEqual Some(Disposition(DispositionAttachment, Some("test")))
		}
		"work with a quoted parameter" in {
			HttpParsers.contentDisposition parseStringOption "attachment; filename=\"test\"" mustEqual Some(Disposition(DispositionAttachment, Some("test")))
		}
		"work with a utf-8 star parameter" in {
			HttpParsers.contentDisposition parseStringOption "attachment; filename*=UTF-8''t%c3%a4st" mustEqual Some(Disposition(DispositionAttachment, Some("täst")))
		}
		"work with a iso-8859-1 star parameter" in {
			HttpParsers.contentDisposition parseStringOption "attachment; filename*=ISO-8859-1''t%e4st" mustEqual Some(Disposition(DispositionAttachment, Some("täst")))
		}
	}
	
	"parsing content type headers" should {
		"work without parameters" in {
			HttpParsers.contentType parseStringOption "text/plain" mustEqual Some(MimeType("text", "plain", NoCaseParameters.empty))
		}
		"work with a token parameter" in {
			HttpParsers.contentType parseStringOption "text/plain; filename=test" mustEqual Some(MimeType("text", "plain", NoCaseParameters(Vector("filename" -> "test"))))
		}
		"work with a quoted parameter" in {
			HttpParsers.contentType parseStringOption "text/plain; filename=\"test\"" mustEqual Some(MimeType("text", "plain", NoCaseParameters(Vector("filename" -> "test"))))
		}
		"work with multiple parameters" in {
			HttpParsers.contentType parseStringOption "text/plain; name=foo; filename=\"bar\"" mustEqual Some(MimeType("text", "plain", NoCaseParameters(Vector("name" -> "foo", "filename" -> "bar"))))
		}
	}
	
	"parsing range headers" should {
		"work for a simple range" in {
			HttpParsers.rangeHeader parseStringOption "bytes=1-2" mustEqual Some(Nes single RangeFromTo(1,2))
		}
		"work for two simple ranges" in {
			HttpParsers.rangeHeader parseStringOption "bytes=1-2,3-4" mustEqual Some(Nes multi (RangeFromTo(1,2), RangeFromTo(3,4)))
		}
		"work for a prefix range" in {
			HttpParsers.rangeHeader parseStringOption "bytes=1-" mustEqual Some(Nes single RangeBegin(1))
		}
		"work for a suffix range" in {
			HttpParsers.rangeHeader parseStringOption "bytes=-2" mustEqual Some(Nes single RangeEnd(2))
		}
		"allow whitespace" in {
			HttpParsers.rangeHeader parseStringOption " bytes = 1 - 2 " mustEqual Some(Nes single RangeFromTo(1,2))
		}
	}
	
	"parsing cookie headers" should {
		"work for a simple cookie" in {
			HttpParsers.cookieHeader parseStringOption "foo=bar" mustEqual Some(CaseParameters(Vector("foo" -> "bar")))
		}
		"work for two simple cookies" in {
			HttpParsers.cookieHeader parseStringOption "foo=bar; quux=wibble" mustEqual Some(CaseParameters(Vector("foo" -> "bar", "quux" -> "wibble")))
		}
		"work for a cookie with whitespace" in {
			HttpParsers.cookieHeader parseStringOption " foo=bar " mustEqual Some(CaseParameters(Vector("foo" -> "bar")))
		}
	}
}
