package scwebapp.util

import org.specs2.mutable._

import scwebapp.data.MimeType

class MimeMappingTest extends Specification {
	"forFileName" should {
		"recognize a png" in {
			MimeMapping.default forFileName "test.png" mustEqual
			Some(MimeType("image", "png"))
		}
	}
	"forExtension" should {
		"recognize a png" in {
			MimeMapping.default forExtension "png" mustEqual
			Some(MimeType("image", "png"))
		}
	}
	
	"forExtension" should {
		"recognize a mixed case extension" in {
			MimeMapping.default forExtension "JpEg" mustEqual
			Some(MimeType("image", "jpeg"))
		}
	}
}
