package scwebapp

import org.specs2.mutable._

class HttpUtilTest extends Specification {
	"quoteStar" should {
		"leave alphanumerics alone" in {
			HttpUtil quoteStar "a0m5Z9" mustEqual "UTF-8''a0m5Z9"
		}
		"leave simple characters alone" in {
			HttpUtil quoteStar "!#$&+-.^_`|~" mustEqual "UTF-8''!#$&+-.^_`|~"
		}
		"encode low chars with a single percent" in {
			HttpUtil quoteStar " :\n" mustEqual "UTF-8''%20%3a%0a"
		}
		"encode high chars with two percent" in {
			HttpUtil quoteStar "äöü" mustEqual "UTF-8''%c3%a4%c3%b6%c3%bc"
		}
	}
}
