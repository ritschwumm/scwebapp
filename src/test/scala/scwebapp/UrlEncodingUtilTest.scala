package scwebapp

import org.specs2.mutable._

import scutil.io.Charsets.utf_8

class UrlEncodingUtilTest extends Specification {
	"parsing query parameters should" should {
		"return empty for an empty string" in {
			UrlEncodingUtil parseQueryParameters ("", utf_8) mustEqual
			CaseParameters.empty
		}
		"decode missing = as empty parameter" in {
			UrlEncodingUtil parseQueryParameters ("test", utf_8) mustEqual
			CaseParameters(Vector("test" -> ""))
		}
		"decode a single value" in {
			UrlEncodingUtil parseQueryParameters ("foo=bar", utf_8) mustEqual
			CaseParameters(Vector("foo" -> "bar"))
		}
		"decode multiple values" in {
			UrlEncodingUtil parseQueryParameters ("foo=bar&x=y", utf_8) mustEqual
			CaseParameters(Vector("foo" -> "bar", "x" -> "y"))
		}
		"decode utf-8 in key and value" in {
			UrlEncodingUtil parseQueryParameters ("f%C3%B6%C3%B6=b%C3%A4r", utf_8) mustEqual
			CaseParameters(Vector("föö" -> "bär"))
		}
	}
}
