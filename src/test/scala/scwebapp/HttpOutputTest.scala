package scwebapp

import org.specs2.mutable._

import java.io.ByteArrayOutputStream

import scutil.io.Charsets

class HttpOutputTest extends Specification {
	"HttpOutput" should {
		"write into an OutputStream" in {
			val output	= HttpOutput withOutputStream { ost =>
				val b	= "hallo" getBytes "utf-8"
				ost write (b, 0, b.length)
			}
			val stream	= new ByteArrayOutputStream
			output intoOutputStream stream
			stream.toString mustEqual "hallo"
		}
	}
	"HttpOutput" should {
		"write into a Writer" in {
			val output	= (HttpOutput withWriter Charsets.utf_8) { wr =>
				val b	= "hallo"
				wr write b
				//wr flush ()
			}
			val stream	= new ByteArrayOutputStream
			output intoOutputStream stream
			stream.toString mustEqual "hallo"
		}
	}
	"HttpOutput" should {
		"write a string" in {
			val output	= (HttpOutput writeString Charsets.utf_8)("hallo")
			val stream	= new ByteArrayOutputStream
			output intoOutputStream stream
			stream.toString mustEqual "hallo"
		}
	}
}
