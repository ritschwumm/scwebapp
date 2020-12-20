package scwebapp.format

import minitest._

object HttpUnparsersTest extends SimpleTestSuite {
	test("quoteStar should leave alphanumerics alone") {
		assertEquals(
			HttpUnparsers quoteStar_UTF8 "a0m5Z9",
			"UTF-8''a0m5Z9"
		)
	}

	test("quoteStar should leave simple characters alone") {
		assertEquals(
			HttpUnparsers quoteStar_UTF8 "!#$&+-.^_`|~",
			"UTF-8''!#$&+-.^_`|~"
		)
	}

	test("quoteStar should encode low chars with a single percent") {
		assertEquals(
			HttpUnparsers quoteStar_UTF8 " :\n",
			"UTF-8''%20%3a%0a"
		)
	}

	test("quoteStar should encode high chars with two percent") {
		assertEquals(
			HttpUnparsers quoteStar_UTF8 "äöü",
			"UTF-8''%c3%a4%c3%b6%c3%bc"
		)
	}
}
