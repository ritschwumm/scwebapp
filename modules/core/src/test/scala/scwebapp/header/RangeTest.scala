package scwebapp.header

import minitest.*

import scutil.lang.*

import scwebapp.data.*

object RangeTest extends SimpleTestSuite {
	test("Range should parse a simple range") {
		assertEquals(
			Range.parse("bytes=1-2"),
			Some(Range(Nes.one(RangePattern.FromTo(1,2))))
		)
	}

	test("Range should parse two simple ranges") {
		assertEquals(
			Range.parse("bytes=1-2,3-4"),
			Some(Range(Nes.of(RangePattern.FromTo(1,2), RangePattern.FromTo(3,4))))
		)
	}

	test("Range should parse a prefix range") {
		assertEquals(
			Range.parse("bytes=1-"),
			Some(Range(Nes.one(RangePattern.Begin(1))))
		)
	}

	test("Range should parse a suffix range") {
		assertEquals(
			Range.parse("bytes=-2"),
			Some(Range(Nes.one(RangePattern.End(2))))
		)
	}

	test("Range should parse with whitespace") {
		assertEquals(
			Range.parse(" bytes = 1 - 2 "),
			Some(Range(Nes.one(RangePattern.FromTo(1,2))))
		)
	}
}
