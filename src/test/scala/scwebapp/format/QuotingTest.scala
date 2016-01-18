package scwebapp
package format

import org.specs2.mutable._

class QuotingTest extends Specification {
	"quoteStar" should {
		"leave alphanumerics alone" in {
			Quoting quoteStar_UTF8 "a0m5Z9" mustEqual "UTF-8''a0m5Z9"
		}
		"leave simple characters alone" in {
			Quoting quoteStar_UTF8 "!#$&+-.^_`|~" mustEqual "UTF-8''!#$&+-.^_`|~"
		}
		"encode low chars with a single percent" in {
			Quoting quoteStar_UTF8 " :\n" mustEqual "UTF-8''%20%3a%0a"
		}
		"encode high chars with two percent" in {
			Quoting quoteStar_UTF8 "äöü" mustEqual "UTF-8''%c3%a4%c3%b6%c3%bc"
		}
	}
}
