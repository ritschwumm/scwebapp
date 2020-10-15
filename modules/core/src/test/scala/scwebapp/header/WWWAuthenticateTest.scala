package scwebapp.header

import org.specs2.mutable._

import scwebapp.data._

class WWWAuthenticateTest extends Specification {
	"WWWAuthenticate" should {
		"unparse a simple realm without charset" in {
			WWWAuthenticate unparse WWWAuthenticate(BasicAuthenticate("test", None)) mustEqual
			"Basic realm=\"test\""
		}

		"unparse a simple ream with charset" in {
			WWWAuthenticate unparse WWWAuthenticate(BasicAuthenticate("test", Some("UTF-8"))) mustEqual
			"Basic realm=\"test\", charset=\"UTF-8\""
		}


		"parse a simple realm without charset" in {
			WWWAuthenticate parse "Basic realm=\"test\"" mustEqual
			Some(WWWAuthenticate(BasicAuthenticate("test", None)))
		}

		"parse a simple realm with charset" in {
			WWWAuthenticate parse "Basic realm=\"test\", charset=\"UTF-8\"" mustEqual
			Some(WWWAuthenticate(BasicAuthenticate("test", Some("UTF-8"))))
		}
	}
}
