package scwebapp.header

import org.specs2.mutable._

import scwebapp.data._

class CookieTest extends Specification {
	"CookieValue" should {
		"parse a simple cookie" in {
			Cookie parse "foo=bar" mustEqual
			Some(Cookie(CaseParameters(Vector("foo" -> "bar"))))
		}
		"parse for two simple cookies" in {
			Cookie parse "foo=bar; quux=wibble" mustEqual
			Some(Cookie(CaseParameters(Vector("foo" -> "bar", "quux" -> "wibble"))))
		}
		"parse a cookie with whitespace" in {
			Cookie parse " foo=bar " mustEqual
			Some(Cookie(CaseParameters(Vector("foo" -> "bar"))))
		}
	}
}
