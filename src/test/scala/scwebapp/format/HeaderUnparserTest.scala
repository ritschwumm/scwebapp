package scwebapp
package format

import org.specs2.mutable._

class HeaderUnparserTest extends Specification {
	"setCookieHeader" should {
		"do at least something" in {
			HeaderUnparser setCookieValue ("foo", "bar") mustEqual
			"foo=bar"
		}
	}
	
	"Disposition" should {
		"unparse" in {
			Disposition unparse Disposition(DispositionAttachment, Some("ä")) mustEqual
			"attachment;filename=\"ä\";filename*=UTF-8''%c3%a4"
		}
	}
}
