package scwebapp.header

import org.specs2.mutable._

import scwebapp.data._

class AuthorizationTest extends Specification {
	"Authorization" should {
		"parse basic" in {
			Authorization parse "Basic Zm9vOmJhcg==" mustEqual
			Some(Authorization(BasicAuthorization("foo", "bar")))
		}

		"unparse basic" in {
			Authorization unparse Authorization(BasicAuthorization("foo", "bar")) mustEqual
			"Basic Zm9vOmJhcg=="
		}
	}
}
