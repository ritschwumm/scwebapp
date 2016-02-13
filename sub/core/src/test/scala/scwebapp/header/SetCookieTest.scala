package scwebapp.header

import org.specs2.mutable._

class SetCookieTest extends Specification {
	"SetCookie" should {
		"do at least something" in {
			SetCookie unparse SetCookie("foo", "bar") mustEqual
			"foo=bar"
		}
	}
}
