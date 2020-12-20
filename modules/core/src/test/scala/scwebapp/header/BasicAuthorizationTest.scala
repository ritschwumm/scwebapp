package scwebapp.header

import minitest._

import scwebapp.data._

object AuthorizationTest extends SimpleTestSuite {
	test("Authorization Basic should parse basic") {
		assertEquals(
			Authorization parse "Basic Zm9vOmJhcg==",
			Some(Authorization(BasicAuthorization("foo", "bar")))
		)
	}

	test("Authorization Basic should unparse basic") {
		assertEquals(
			Authorization unparse Authorization(BasicAuthorization("foo", "bar")),
			"Basic Zm9vOmJhcg=="
		)
	}
}
