package scwebapp

import org.specs2.mutable._

import scutil.implicits._

class RequestValueTest extends Specification {
	"parsing request range should" should {
		"work for a from-to range" in {
			 RangeFromTo(1,2) into (RequestRange parse 10) mustEqual Some(RequestRange(InclusiveRange(1,2), 10))
		}
		"work for a prefix range" in {
			RangeBegin(1) into (RequestRange parse 10) mustEqual Some(RequestRange(InclusiveRange(1,9), 10))
		}
		"work for a suffix range" in {
			RangeEnd(2) into (RequestRange parse 10) mustEqual Some(RequestRange(InclusiveRange(8,9), 10))
		}
		"work for a close suffix" in {
			RangeEnd(2) into (RequestRange parse 2) mustEqual Some(RequestRange(InclusiveRange(0,1), 2))
		}
		"fail with an overstepped end range" in {
			RangeBegin(2) into (RequestRange parse 2) mustEqual None
		}
		"fail with an overstepped begin range" in {
			RangeEnd(2) into (RequestRange parse 1) mustEqual None
		}
	}
}
