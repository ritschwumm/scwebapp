package scwebapp
package format

import org.specs2.mutable._

import scutil.io.Charsets.utf_8

import scwebapp.data._

class UrlEncodingTest extends Specification {
	"parsing query parameters should" should {
		"return empty for an empty string" in {
			UrlEncoding parseQueryParameters ("", utf_8) mustEqual
			CaseParameters.empty
		}
		"decode missing = as empty parameter" in {
			UrlEncoding parseQueryParameters ("test", utf_8) mustEqual
			CaseParameters(Vector("test" -> ""))
		}
		"decode a single value" in {
			UrlEncoding parseQueryParameters ("foo=bar", utf_8) mustEqual
			CaseParameters(Vector("foo" -> "bar"))
		}
		"decode multiple values" in {
			UrlEncoding parseQueryParameters ("foo=bar&x=y", utf_8) mustEqual
			CaseParameters(Vector("foo" -> "bar", "x" -> "y"))
		}
		"decode utf-8 in key and value" in {
			UrlEncoding parseQueryParameters ("f%C3%B6%C3%B6=b%C3%A4r", utf_8) mustEqual
			CaseParameters(Vector("föö" -> "bär"))
		}
	}
}
