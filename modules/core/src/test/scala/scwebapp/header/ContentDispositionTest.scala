package scwebapp.header

import minitest._

import scwebapp.data._

object ContentDispositionTest extends SimpleTestSuite {
	test("ContentDisposition should unparse") {
		assertEquals(
			ContentDisposition unparse ContentDisposition(ContentDispositionType.Attachment,
			Some("ä")), "attachment;filename=ä;filename*=UTF-8''%c3%a4"
		)
	}

	test("ContentDisposition should parse without parameters") {
		assertEquals(
			ContentDisposition parse "attachment",
			Some(ContentDisposition(ContentDispositionType.Attachment,  None))
		)
	}

	test("ContentDisposition should parse with a token parameter") {
		assertEquals(
			ContentDisposition parse "attachment; filename=test",
			Some(ContentDisposition(ContentDispositionType.Attachment, Some("test")))
		)
	}

	test("ContentDisposition should parse with a quoted parameter") {
		assertEquals(
			ContentDisposition parse "attachment; filename=\"test\"",
			Some(ContentDisposition(ContentDispositionType.Attachment, Some("test")))
		)
	}

	test("ContentDisposition should parse with a utf-8 star parameter") {
		assertEquals(
			ContentDisposition parse "attachment; filename*=UTF-8''t%c3%a4st",
			Some(ContentDisposition(ContentDispositionType.Attachment, Some("täst")))
		)
	}

	test("ContentDisposition should parse with a iso-8859-1 star parameter") {
		assertEquals(
			ContentDisposition parse "attachment; filename*=ISO-8859-1''t%e4st",
			Some(ContentDisposition(ContentDispositionType.Attachment, Some("täst")))
		)
	}

	test("ContentDisposition should parse mixed parameters") {
		assertEquals(
			ContentDisposition parse "attachment; filename=foo; filename*=UTF-8''t%c3%a4st",
			Some(ContentDisposition(ContentDispositionType.Attachment, Some("täst")))
		)
	}

	test("ContentDisposition should parse mixed parameters") {
		assertEquals(
			ContentDisposition parse "attachment; filename*=UTF-8''t%c3%a4st; filename=foo",
			Some(ContentDisposition(ContentDispositionType.Attachment, Some("täst")))
		)
	}
}
