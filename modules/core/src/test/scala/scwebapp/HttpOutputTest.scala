package scwebapp

import minitest.*

import java.io.ByteArrayOutputStream

import scutil.lang.Charsets

@SuppressWarnings(Array("org.wartremover.warts.ToString"))
object HttpOutputTest extends SimpleTestSuite {
	test("HttpOutput should write into an OutputStream") {
		val output	= HttpOutput withOutputStream { ost =>
			val b	= "hallo".getBytes("utf-8")
			ost.write(b, 0, b.length)
		}
		val stream	= new ByteArrayOutputStream
		output.intoOutputStream(stream)
		assertEquals(stream.toString, "hallo")
	}

	test("HttpOutput should write into a Writer") {
		val output	= HttpOutput.withWriter(Charsets.utf_8) { wr =>
			val b	= "hallo"
			wr.write(b)
			//wr.flush()
		}
		val stream	= new ByteArrayOutputStream
		output.intoOutputStream(stream)
		assertEquals(stream.toString, "hallo")
	}

	test("HttpOutput should write a string") {
		val output	= HttpOutput.writeString(Charsets.utf_8, "hallo")
		val stream	= new ByteArrayOutputStream
		output.intoOutputStream(stream)
		assertEquals(stream.toString, "hallo")
	}
}
