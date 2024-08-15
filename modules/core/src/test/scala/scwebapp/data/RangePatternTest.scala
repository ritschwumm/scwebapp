package scwebapp.data

import minitest.*

object RangePatternTest extends SimpleTestSuite {
	test("parsing request range should work for a from-to range") {
		assertEquals(
			RangePattern.FromTo(1, 2).toInclusiveRange(10),
			Some(InclusiveRange(1, 2))
		)
	}
	test("parsing request range should work for a prefix range") {
		assertEquals(
			RangePattern.Begin(1).toInclusiveRange(10),
			Some(InclusiveRange(1, 9))
		)
	}
	test("parsing request range should work for a suffix range") {
		assertEquals(
			RangePattern.End(2).toInclusiveRange(10),
			Some(InclusiveRange(8, 9))
		)
	}
	test("parsing request range should work for a close suffix") {
		assertEquals(
			RangePattern.End(2).toInclusiveRange(2),
			Some(InclusiveRange(0,1))
		)
	}
	test("parsing request range should fail with an overstepped end range") {
		assertEquals(
			RangePattern.Begin(2).toInclusiveRange(2),
			None
		)
	}
	test("parsing request range should fail with an overstepped begin range") {
		assertEquals(
			RangePattern.End(2).toInclusiveRange(1),
			None
		)
	}
}
