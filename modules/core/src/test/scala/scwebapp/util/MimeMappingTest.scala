package scwebapp.util

import minitest._

import scwebapp.data.MimeType

object MimeMappingTest extends SimpleTestSuite {
	test("MimeMapping forFileName should") {
		assertEquals(
			MimeMapping.default forFileName "test.png",
			Some(MimeType("image", "png"))
		)
	}

	test("MimeMapping forExtension should") {
		assertEquals(
			MimeMapping.default forExtension "png",
			Some(MimeType("image", "png"))
		)
	}

	test("MimeMapping forExtension should") {
		assertEquals(
			MimeMapping.default forExtension "JpEg",
			Some(MimeType("image", "jpeg"))
		)
	}
}
