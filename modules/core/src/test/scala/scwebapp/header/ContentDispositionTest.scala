package scwebapp.header

import org.specs2.mutable._

import scwebapp.data._

class ContentDispositionTest extends Specification {
	"ContentDisposition" should {
		"unparse" in {
			ContentDisposition unparse ContentDisposition(ContentDispositionAttachment, Some("ä")) mustEqual
			"attachment;filename=ä;filename*=UTF-8''%c3%a4"
		}

		"parse without parameters" in {
			ContentDisposition parse "attachment" mustEqual
			Some(ContentDisposition(ContentDispositionAttachment,  None))
		}
		"parse with a token parameter" in {
			ContentDisposition parse "attachment; filename=test" mustEqual
			Some(ContentDisposition(ContentDispositionAttachment, Some("test")))
		}
		"parse with a quoted parameter" in {
			ContentDisposition parse "attachment; filename=\"test\"" mustEqual
			Some(ContentDisposition(ContentDispositionAttachment, Some("test")))
		}
		"parse with a utf-8 star parameter" in {
			ContentDisposition parse "attachment; filename*=UTF-8''t%c3%a4st" mustEqual
			Some(ContentDisposition(ContentDispositionAttachment, Some("täst")))
		}
		"parse with a iso-8859-1 star parameter" in {
			ContentDisposition parse "attachment; filename*=ISO-8859-1''t%e4st" mustEqual
			Some(ContentDisposition(ContentDispositionAttachment, Some("täst")))
		}
		"parse mixed parameters" in {
			ContentDisposition parse "attachment; filename=foo; filename*=UTF-8''t%c3%a4st" mustEqual
			Some(ContentDisposition(ContentDispositionAttachment, Some("täst")))
		}
		"parse mixed parameters" in {
			ContentDisposition parse "attachment; filename*=UTF-8''t%c3%a4st; filename=foo" mustEqual
			Some(ContentDisposition(ContentDispositionAttachment, Some("täst")))
		}
	}
}
