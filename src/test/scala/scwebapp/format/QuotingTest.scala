package scwebapp
package format

import org.specs2.mutable._

class QuotingTest extends Specification {
	"quoteStar" should {
		"leave alphanumerics alone" in {
			Quoting quoteStar "a0m5Z9" mustEqual "UTF-8''a0m5Z9"
		}
		"leave simple characters alone" in {
			Quoting quoteStar "!#$&+-.^_`|~" mustEqual "UTF-8''!#$&+-.^_`|~"
		}
		"encode low chars with a single percent" in {
			Quoting quoteStar " :\n" mustEqual "UTF-8''%20%3a%0a"
		}
		"encode high chars with two percent" in {
			Quoting quoteStar "äöü" mustEqual "UTF-8''%c3%a4%c3%b6%c3%bc"
		}
	}
}
