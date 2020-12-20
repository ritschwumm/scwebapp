package scwebapp.header

import minitest._

import scwebapp.data._

object WWWAuthenticateTest extends SimpleTestSuite {
	test("WWWAuthenticate should unparse a simple realm without charset") {
		assertEquals(
			WWWAuthenticate unparse WWWAuthenticate(BasicAuthenticate("test", None)),
			"Basic realm=\"test\""
		)
	}

	test("WWWAuthenticate should unparse a simple ream with charset") {
		assertEquals(
			WWWAuthenticate unparse WWWAuthenticate(BasicAuthenticate("test", Some("UTF-8"))),
			"Basic realm=\"test\", charset=\"UTF-8\""
		)
	}


	test("WWWAuthenticate should parse a simple realm without charset") {
		assertEquals(
			WWWAuthenticate parse "Basic realm=\"test\"",
			Some(WWWAuthenticate(BasicAuthenticate("test", None)))
		)
	}

	test("WWWAuthenticate should parse a simple realm with charset") {
		assertEquals(
			WWWAuthenticate parse "Basic realm=\"test\", charset=\"UTF-8\"",
			Some(WWWAuthenticate(BasicAuthenticate("test", Some("UTF-8"))))
		)
	}
}
