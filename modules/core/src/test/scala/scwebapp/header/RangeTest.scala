package scwebapp.header

import org.specs2.mutable._

import scutil.lang._

import scwebapp.data._

class RangeTest extends Specification {
	"Range" should {
		"parse a simple range" in {
			Range parse "bytes=1-2" mustEqual
			Some(Range(Nes single RangeFromTo(1,2)))
		}
		"parse two simple ranges" in {
			Range parse "bytes=1-2,3-4" mustEqual
			Some(Range(Nes multi (RangeFromTo(1,2), RangeFromTo(3,4))))
		}
		"parse a prefix range" in {
			Range parse "bytes=1-" mustEqual
			Some(Range(Nes single RangeBegin(1)))
		}
		"parse a suffix range" in {
			Range parse "bytes=-2" mustEqual
			Some(Range(Nes single RangeEnd(2)))
		}
		"parse with whitespace" in {
			Range parse " bytes = 1 - 2 " mustEqual
			Some(Range(Nes single RangeFromTo(1,2)))
		}
	}
}
