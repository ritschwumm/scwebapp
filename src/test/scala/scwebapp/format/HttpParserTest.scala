package scwebapp
package format

import org.specs2.mutable._

import scutil.lang.Nes

import scwebapp.parser.string._

class HttpParserTest extends Specification {
	"basic parser should" should {
		"parse an empty quoted string" in {
			HttpParser.quotedString parseStringOption "\"\"" mustEqual Some("")
		}
		"parse an OCTET" in {
			HttpParser.OCTET parseStringOption "t" mustEqual Some('t')
		}
		"parse a TEXT" in {
			HttpParser.TEXT parseStringOption "t" mustEqual Some('t')
		}
		"parse a dqText" in {
			HttpParser.dqText parseStringOption "t" mustEqual Some('t')
		}
		"parse a quotedChar" in {
			HttpParser.quotedChar parseStringOption "t" mustEqual Some('t')
		}
		"parse a quotedString" in {
			HttpParser.quotedString parseStringOption "\"test\"" mustEqual Some("test")
		}
	}
		
	"parsing content disposition headers" should {
		"work without parameters" in {
			HttpParser.contentDisposition parseStringOption "attachment" mustEqual Some(("attachment", Vector.empty))
		}
		"work with a token parameter" in {
			HttpParser.contentDisposition parseStringOption "attachment; filename=test" mustEqual Some(("attachment", Vector("filename" -> "test")))
		}
		"work with a quoted parameter" in {
			HttpParser.contentDisposition parseStringOption "attachment; filename=\"test\"" mustEqual Some(("attachment", Vector("filename" -> "test")))
		}
		"work with multiple parameters" in {
			HttpParser.contentDisposition parseStringOption "attachment; name=foo; filename=\"bar\"" mustEqual Some(("attachment", Vector("name" -> "foo", "filename" -> "bar")))
		}
	}
	
	"parsing content type headers" should {
		"work without parameters" in {
			HttpParser.contentType parseStringOption "text/plain" mustEqual Some((("text", "plain"), Vector.empty))
		}
		"work with a token parameter" in {
			HttpParser.contentType parseStringOption "text/plain; filename=test" mustEqual Some((("text", "plain"), Vector("filename" -> "test")))
		}
		"work with a quoted parameter" in {
			HttpParser.contentType parseStringOption "text/plain; filename=\"test\"" mustEqual Some((("text", "plain"), Vector("filename" -> "test")))
		}
		"work with multiple parameters" in {
			HttpParser.contentType parseStringOption "text/plain; name=foo; filename=\"bar\"" mustEqual Some((("text", "plain"), Vector("name" -> "foo", "filename" -> "bar")))
		}
	}
	
	"parsing range headers" should {
		"work for a simple range" in {
			HttpParser.rangeHeader parseStringOption "bytes=1-2" mustEqual Some(Nes multi OneAndTwo(1L, 2L))
		}
		"work for two simple ranges" in {
			HttpParser.rangeHeader parseStringOption "bytes=1-2,3-4" mustEqual Some(Nes multi (OneAndTwo(1L, 2L), OneAndTwo(3L, 4L)))
		}
		"work for a prefix range" in {
			HttpParser.rangeHeader parseStringOption "bytes=1-" mustEqual Some(Nes multi One(1L))
		}
		"work for a suffix range" in {
			HttpParser.rangeHeader parseStringOption "bytes=-2" mustEqual Some(Nes multi Two(2L))
		}
		"allow whitespace" in {
			HttpParser.rangeHeader parseStringOption " bytes = 1 - 2 " mustEqual Some(Nes multi OneAndTwo(1L, 2L))
		}
	}
	
	"parsing cookie headers" should {
		"work for a simple cookie" in {
			HttpParser.cookieHeader parseStringOption "foo=bar" mustEqual Some(Seq("foo" -> "bar"))
		}
		"work for two simple cookies" in {
			HttpParser.cookieHeader parseStringOption "foo=bar; quux=wibble" mustEqual Some(Seq("foo" -> "bar", "quux" -> "wibble"))
		}
		"work for a cookie with whitespace" in {
			HttpParser.cookieHeader parseStringOption " foo=bar " mustEqual Some(Seq("foo" -> "bar"))
		}
	}
}
