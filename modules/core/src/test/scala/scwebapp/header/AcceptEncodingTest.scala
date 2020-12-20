package scwebapp.header

import minitest._

import scwebapp.data._

object AcceptEncodingTest extends SimpleTestSuite {
	test("AcceptEncoding should parse empty") {
		assertEquals(
			AcceptEncoding parse "",
			Some(AcceptEncoding(Vector.empty))
		)
	}

	test("AcceptEncoding should parse any") {
		assertEquals(
			AcceptEncoding parse "*",
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingPattern.Wildcard,None))))
		)
	}

	test("AcceptEncoding should parse any with quality") {
		assertEquals(
			AcceptEncoding parse "*;q=1",
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingPattern.Wildcard,Some(QValue.one)))))
		)
	}

	test("AcceptEncoding should parse identity") {
		assertEquals(
			AcceptEncoding parse "identity",
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingPattern.Fixed(AcceptEncodingType.Identity),None))))
		)
	}

	test("AcceptEncoding should parse gzip") {
		assertEquals(
			AcceptEncoding parse "gzip",
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingPattern.Fixed(AcceptEncodingType.Other(ContentEncodingType.Gzip)),None))))
		)
	}

	test("AcceptEncoding should accept identity when empty") {
		assertEquals(
			AcceptEncoding parse "" map { _ acceptance AcceptEncodingType.Identity },
			Some(QValue(1000))
		)
	}

	test("AcceptEncoding should not accept gzip when empty") {
		assertEquals(
			AcceptEncoding parse "" map { _ acceptance AcceptEncodingType.Other(ContentEncodingType.Gzip) },
			Some(QValue(0))
		)
	}
	test("AcceptEncoding should accept gzip with star") {
		assertEquals(
			AcceptEncoding parse "*;q=0.5" map { _ acceptance AcceptEncodingType.Other(ContentEncodingType.Gzip) },
			Some(QValue(500))
		)
	}

	test("AcceptEncoding should accept gzip with quality") {
		assertEquals(
			AcceptEncoding parse "gzip;q=0.5" map { _ acceptance AcceptEncodingType.Other(ContentEncodingType.Gzip) },
			Some(QValue(500))
		)
	}

	test("AcceptEncoding should override specific with wildcard") {
		assertEquals(
			AcceptEncoding parse "*;q=0.7,gzip;q=0.5" map { _ acceptance AcceptEncodingType.Other(ContentEncodingType.Gzip) },
			Some(QValue(500))
		)
	}
}
