package scwebapp.header

import minitest._

object SetCookieTest extends SimpleTestSuite {
	test("SetCookie should do at least something") {
		assertEquals(
			SetCookie unparse SetCookie("foo", "bar"),
			"foo=bar"
		)
	}

	test("SetCookie should unparse correctly") {
		assertEquals(
			SetCookie unparse SetCookie(name="session", value="56e5ffe283148bab2138c154", path=Some("/")),
			"session=56e5ffe283148bab2138c154;Path=/"
		)
	}
}
