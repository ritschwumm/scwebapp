package scwebapp.util

import org.specs2.mutable._

import scwebapp.data.MimeType

class MimeTypeUtilTest extends Specification {
	"forFileName" should {
		"recognize a png" in {
			MimeTypeUtil forFileName "test.png" mustEqual
			Some(MimeType("image", "png"))
		}
	}
	"forExtension" should {
		"recognize a png" in {
			MimeTypeUtil forExtension "png" mustEqual
			Some(MimeType("image", "png"))
		}
	}
}
