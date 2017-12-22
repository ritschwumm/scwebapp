package scwebapp.data

import org.specs2.mutable._

class RangePatternTest extends Specification {
	"parsing request range should" should {
		"work for a from-to range" in {
			 RangeFromTo(1, 2) toInclusiveRange 10 mustEqual
			 Some(InclusiveRange(1, 2))
		}
		"work for a prefix range" in {
			RangeBegin(1) toInclusiveRange 10 mustEqual
			Some(InclusiveRange(1, 9))
		}
		"work for a suffix range" in {
			RangeEnd(2) toInclusiveRange 10 mustEqual
			Some(InclusiveRange(8, 9))
		}
		"work for a close suffix" in {
			RangeEnd(2) toInclusiveRange 2 mustEqual
			Some(InclusiveRange(0,1))
		}
		"fail with an overstepped end range" in {
			RangeBegin(2) toInclusiveRange 2 mustEqual
			None
		}
		"fail with an overstepped begin range" in {
			RangeEnd(2) toInclusiveRange 1 mustEqual
			None
		}
	}
}
