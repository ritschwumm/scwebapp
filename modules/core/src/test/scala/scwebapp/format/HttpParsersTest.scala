package scwebapp.format

import minitest.*

import scutil.lang.Charsets

import scparse.ng.text.*

object HttpParsersTest extends SimpleTestSuite {
	test("star parameters should parse a star parameter name") {
		assertEquals(
			HttpParsers.extParName.parseString("filename*").toOption,
			Some("filename")
		)
	}

	test("star parameters should parse a star parameter charset") {
		assertEquals(
			HttpParsers.charset.parseString("UTF-8").toOption,
			Some(Some(Charsets.utf_8))
		)
	}

	test("star parameters should parse a star parameter language") {
		assertEquals(
			HttpParsers.language.parseString("").toOption,
			Some("")
		)
	}

	test("star parameters should parse a star parameter's bytes") {
		assertEquals(
			HttpParsers.valueCharBytes.parseString("t%c3%a4st").toOption map { _.toVector },
			Some("täst".getBytes("utf-8").toVector)
		)
	}

	test("star parameters should parse a star parameter value") {
		assertEquals(
			HttpParsers.extValue.parseString("UTF-8''t%c3%a4st").toOption,
			Some("täst")
		)
	}

	test("star parameters should parse a star parameter value") {
		assertEquals(
			HttpParsers.extValue.parseString("ISO-8859-1''t%e4st").toOption,
			Some("täst")
		)
	}

	test("star parameters should parse a star parameter") {
		assertEquals(
			HttpParsers.extParameter.parseString("filename*=UTF-8''t%c3%a4st").toOption,
			Some((true, ("filename", "täst")))
		)
	}

	//------------------------------------------------------------------------------

	test("basic parser should parse an empty quoted string") {
		assertEquals(
			HttpParsers.quotedString.parseString("\"\"").toOption,
			Some("")
		)
	}

	test("basic parser should parse an OCTET") {
		assertEquals(
			HttpParsers.OCTET.parseString("t").toOption,
			Some('t')
		)
	}

	test("basic parser should parse a TEXT") {
		assertEquals(
			HttpParsers.TEXT.parseString("t").toOption,
			Some('t')
		)
	}

	test("basic parser should parse a dqText") {
		assertEquals(
			HttpParsers.dqText.parseString("t").toOption,
			Some('t')
		)
	}

	test("basic parser should parse a quotedChar") {
		assertEquals(
			HttpParsers.quotedChar.parseString("t").toOption,
			Some('t')
		)
	}

	test("basic parser should parse a quotedString") {
		assertEquals(
			HttpParsers.quotedString.parseString("\"test\"").toOption,
			Some("test")
		)
	}

	test("basic parser should parse a positive long") {
		assertEquals(
			HttpParsers.longPositive.parseString("147").toOption,
			Some(147L)
		)
	}
}
