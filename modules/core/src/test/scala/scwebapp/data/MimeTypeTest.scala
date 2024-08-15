package scwebapp.data

import minitest.*

object MimeTypeTest extends SimpleTestSuite {
	test("MimeType should parse without parameters") {
		assertEquals(
			MimeType.parse("text/plain"),
			Some(MimeType("text", "plain", NoCaseParameters.empty))
		)
	}

	test("MimeType should parse with a token parameter") {
		assertEquals(
			MimeType.parse("text/plain; filename=test"),
			Some(MimeType("text", "plain", NoCaseParameters(Vector("filename" -> "test"))))
		)
	}
	test("MimeType should parse with a quoted parameter") {
		assertEquals(
			MimeType.parse("text/plain; filename=\"test\""),
			Some(MimeType("text", "plain", NoCaseParameters(Vector("filename" -> "test"))))
		)
	}
	test("MimeType should parse with multiple parameters") {
		assertEquals(
			MimeType.parse("text/plain; name=foo; filename=\"bar\""),
			Some(MimeType("text", "plain", NoCaseParameters(Vector("name" -> "foo", "filename" -> "bar"))))
		)
	}
}
