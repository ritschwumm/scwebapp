package scwebapp.header

import org.specs2.mutable._

import scwebapp.data._

class WWWAuthenticateTest extends Specification {
	"WWWAuthenticate" should {
		"unparse a simple realm" in {
			WWWAuthenticate unparse WWWAuthenticate(BasicAuthenticate("test")) mustEqual
			"Basic realm=\"test\""
		}

		"parse a simple realm" in {
			WWWAuthenticate parse "Basic realm=\"test\"" mustEqual
			Some(WWWAuthenticate(BasicAuthenticate("test")))
		}
	}
}
