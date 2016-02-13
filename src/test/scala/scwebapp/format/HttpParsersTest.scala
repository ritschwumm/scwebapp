package scwebapp.format

import org.specs2.mutable._

import scutil.lang.Nes
import scutil.io.Charsets

import scwebapp.parser.string._

class HttpParsersTest extends Specification {
	"star parameters" should {
		"parse a star parameter name" in {
			HttpParsers.extParName parseStringOption "filename*" mustEqual
			Some("filename")
		}
		"parse a star parameter charset" in {
			HttpParsers.charset parseStringOption "UTF-8" mustEqual
			Some(Some(Charsets.utf_8))
		}
		"parse a star parameter language" in {
			HttpParsers.language parseStringOption "" mustEqual
			Some("")
		}
		"parse a star parameter's bytes" in {
			HttpParsers.valueCharBytes parseStringOption "t%c3%a4st" map { _.toVector } mustEqual
			Some(("täst" getBytes "utf-8").toVector)
		}
		"parse a star parameter value" in {
			HttpParsers.extValue parseStringOption "UTF-8''t%c3%a4st" mustEqual
			Some("täst")
		}
		"parse a star parameter value" in {
			HttpParsers.extValue parseStringOption "ISO-8859-1''t%e4st" mustEqual
			Some("täst")
		}
		"parse a star parameter" in {
			HttpParsers.extParameter parseStringOption "filename*=UTF-8''t%c3%a4st" mustEqual
			Some((true, ("filename", "täst")))
		}
	}
	
	"basic parser should" should {
		"parse an empty quoted string" in {
			HttpParsers.quotedString parseStringOption "\"\"" mustEqual
			Some("")
		}
		"parse an OCTET" in {
			HttpParsers.OCTET parseStringOption "t" mustEqual
			Some('t')
		}
		"parse a TEXT" in {
			HttpParsers.TEXT parseStringOption "t" mustEqual
			Some('t')
		}
		"parse a dqText" in {
			HttpParsers.dqText parseStringOption "t" mustEqual
			Some('t')
		}
		"parse a quotedChar" in {
			HttpParsers.quotedChar parseStringOption "t" mustEqual
			Some('t')
		}
		"parse a quotedString" in {
			HttpParsers.quotedString parseStringOption "\"test\"" mustEqual
			Some("test")
		}
	}
}
