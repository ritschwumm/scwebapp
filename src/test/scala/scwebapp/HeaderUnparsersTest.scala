package scwebapp

import org.specs2.mutable._

class HeaderUnparsersTest extends Specification {
	"setCookieHeader" should {
		"do at least something" in {
			HeaderUnparsers setCookieHeader ("foo", "bar") mustEqual "foo=bar"
		}
	}
		
	"quoteStar" should {
		"leave alphanumerics alone" in {
			HeaderUnparsers quoteStar "a0m5Z9" mustEqual "UTF-8''a0m5Z9"
		}
		"leave simple characters alone" in {
			HeaderUnparsers quoteStar "!#$&+-.^_`|~" mustEqual "UTF-8''!#$&+-.^_`|~"
		}
		"encode low chars with a single percent" in {
			HeaderUnparsers quoteStar " :\n" mustEqual "UTF-8''%20%3a%0a"
		}
		"encode high chars with two percent" in {
			HeaderUnparsers quoteStar "äöü" mustEqual "UTF-8''%c3%a4%c3%b6%c3%bc"
		}
	}
}
