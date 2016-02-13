package scwebapp.data

import org.specs2.mutable._

class MimeTypeTest extends Specification {
	"MimeType" should {
		"parse without parameters" in {
			MimeType parse "text/plain" mustEqual
			Some(MimeType("text", "plain", NoCaseParameters.empty))
		}
		"parse with a token parameter" in {
			MimeType parse "text/plain; filename=test" mustEqual
			Some(MimeType("text", "plain", NoCaseParameters(Vector("filename" -> "test"))))
		}
		"parse with a quoted parameter" in {
			MimeType parse "text/plain; filename=\"test\"" mustEqual
			Some(MimeType("text", "plain", NoCaseParameters(Vector("filename" -> "test"))))
		}
		"parse with multiple parameters" in {
			MimeType parse "text/plain; name=foo; filename=\"bar\"" mustEqual
			Some(MimeType("text", "plain", NoCaseParameters(Vector("name" -> "foo", "filename" -> "bar"))))
		}
	}
}
