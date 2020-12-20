package scwebapp.header

import minitest._

import scutil.lang._

import scwebapp.data._

object RangeTest extends SimpleTestSuite {
	test("Range should parse a simple range") {
		assertEquals(
			Range parse "bytes=1-2",
			Some(Range(Nes single RangePattern.FromTo(1,2)))
		)
	}

	test("Range should parse two simple ranges") {
		assertEquals(
			Range parse "bytes=1-2,3-4",
			Some(Range(Nes.of(RangePattern.FromTo(1,2), RangePattern.FromTo(3,4))))
		)
	}

	test("Range should parse a prefix range") {
		assertEquals(
			Range parse "bytes=1-",
			Some(Range(Nes single RangePattern.Begin(1)))
		)
	}

	test("Range should parse a suffix range") {
		assertEquals(
			Range parse "bytes=-2",
			Some(Range(Nes single RangePattern.End(2)))
		)
	}

	test("Range should parse with whitespace") {
		assertEquals(
			Range parse " bytes = 1 - 2 ",
			Some(Range(Nes single RangePattern.FromTo(1,2)))
		)
	}
}
