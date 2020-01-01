package scwebapp.format

import org.specs2.mutable._

import scutil.lang.Charsets

import scparse.ng.text._

class HttpParsersTest extends Specification {
	"star parameters" should {
		"parse a star parameter name" in {
			HttpParsers.extParName.parseString("filename*").toOption mustEqual
			Some("filename")
		}
		"parse a star parameter charset" in {
			HttpParsers.charset.parseString("UTF-8").toOption mustEqual
			Some(Some(Charsets.utf_8))
		}
		"parse a star parameter language" in {
			HttpParsers.language.parseString("").toOption mustEqual
			Some("")
		}
		"parse a star parameter's bytes" in {
			HttpParsers.valueCharBytes.parseString("t%c3%a4st").toOption map { _.toVector } mustEqual
			Some(("täst" getBytes "utf-8").toVector)
		}
		"parse a star parameter value" in {
			HttpParsers.extValue.parseString("UTF-8''t%c3%a4st").toOption mustEqual
			Some("täst")
		}
		"parse a star parameter value" in {
			HttpParsers.extValue.parseString("ISO-8859-1''t%e4st").toOption mustEqual
			Some("täst")
		}
		"parse a star parameter" in {
			HttpParsers.extParameter.parseString("filename*=UTF-8''t%c3%a4st").toOption mustEqual
			Some((true, ("filename", "täst")))
		}
	}

	"basic parser should" should {
		"parse an empty quoted string" in {
			HttpParsers.quotedString.parseString("\"\"").toOption mustEqual
			Some("")
		}
		"parse an OCTET" in {
			HttpParsers.OCTET.parseString("t").toOption mustEqual
			Some('t')
		}
		"parse a TEXT" in {
			HttpParsers.TEXT.parseString("t").toOption mustEqual
			Some('t')
		}
		"parse a dqText" in {
			HttpParsers.dqText.parseString("t").toOption mustEqual
			Some('t')
		}
		"parse a quotedChar" in {
			HttpParsers.quotedChar.parseString("t").toOption mustEqual
			Some('t')
		}
		"parse a quotedString" in {
			HttpParsers.quotedString.parseString("\"test\"").toOption mustEqual
			Some("test")
		}
		"parse a positive long" in {
			HttpParsers.longPositive.parseString("147").toOption mustEqual
			Some(147L)
		}
	}
}
