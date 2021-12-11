package scwebapp.format

import minitest.*

import scutil.lang.Charsets.utf_8

import scwebapp.data.*

object UrlEncodingTest extends SimpleTestSuite {
	test("parsing query parameters should return empty for an empty string") {
		assertEquals(
			UrlEncoding.parseQueryParameters("", utf_8),
			Right(CaseParameters.empty)
		)
	}

	test("parsing query parameters should decode missing = as empty parameter") {
		assertEquals(
			UrlEncoding.parseQueryParameters("test", utf_8),
			Right(CaseParameters(Vector("test" -> "")))
		)
	}

	test("parsing query parameters should decode a single value") {
		assertEquals(
			UrlEncoding.parseQueryParameters("foo=bar", utf_8),
			Right(CaseParameters(Vector("foo" -> "bar")))
		)
	}

	test("parsing query parameters should decode multiple values") {
		assertEquals(
			UrlEncoding.parseQueryParameters("foo=bar&x=y", utf_8),
			Right(CaseParameters(Vector("foo" -> "bar", "x" -> "y")))
		)
	}

	test("parsing query parameters should decode utf-8 in key and value") {
		assertEquals(
			UrlEncoding.parseQueryParameters("f%C3%B6%C3%B6=b%C3%A4r", utf_8),
			Right(CaseParameters(Vector("föö" -> "bär")))
		)
	}
}
