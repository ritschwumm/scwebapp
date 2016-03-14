package scwebapp.header

import org.specs2.mutable._

class SetCookieTest extends Specification {
	"SetCookie" should {
		"do at least something" in {
			SetCookie unparse SetCookie("foo", "bar") mustEqual
			"foo=bar"
		}
		"unparse correctly" in {
			SetCookie unparse SetCookie(name="session", value="56e5ffe283148bab2138c154", path=Some("/")) mustEqual
			"session=56e5ffe283148bab2138c154;Path=/"
		}
	}
}
