package scwebapp.data

import org.specs2.mutable._

class RangePatternTest extends Specification {
	"parsing request range should" should {
		"work for a from-to range" in {
			 RangePattern.FromTo(1, 2) toInclusiveRange 10 mustEqual
			 Some(InclusiveRange(1, 2))
		}
		"work for a prefix range" in {
			RangePattern.Begin(1) toInclusiveRange 10 mustEqual
			Some(InclusiveRange(1, 9))
		}
		"work for a suffix range" in {
			RangePattern.End(2) toInclusiveRange 10 mustEqual
			Some(InclusiveRange(8, 9))
		}
		"work for a close suffix" in {
			RangePattern.End(2) toInclusiveRange 2 mustEqual
			Some(InclusiveRange(0,1))
		}
		"fail with an overstepped end range" in {
			RangePattern.Begin(2) toInclusiveRange 2 mustEqual
			None
		}
		"fail with an overstepped begin range" in {
			RangePattern.End(2) toInclusiveRange 1 mustEqual
			None
		}
	}
}
