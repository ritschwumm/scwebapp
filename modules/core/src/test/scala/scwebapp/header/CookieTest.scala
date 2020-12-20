package scwebapp.header

import minitest._

import scwebapp.data._

object CookieTest extends SimpleTestSuite {
	test("Cookie should parse a simple cookie") {
		assertEquals(
			Cookie parse "foo=bar",
			Some(Cookie(CaseParameters(Vector("foo" -> "bar"))))
		)
	}

	test("Cookie should parse for two simple cookies") {
		assertEquals(
			Cookie parse "foo=bar; quux=wibble",
			Some(Cookie(CaseParameters(Vector("foo" -> "bar", "quux" -> "wibble"))))
		)
	}

	test("Cookie should parse a cookie with whitespace") {
		assertEquals(
			Cookie parse " foo=bar ",
			Some(Cookie(CaseParameters(Vector("foo" -> "bar"))))
		)
	}
}
