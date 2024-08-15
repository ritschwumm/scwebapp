package scwebapp.data

import minitest.*

import scwebapp.format.*
import scparse.ng.text.*

object QValueTest extends SimpleTestSuite {
	val parser	= QValue.parser.finishRight(HttpParsers.LWSP)
	def parseQValue(it:String):Option[QValue] = parser.parseString(it).toOption

	test("QValue should parse 0") {
		assertEquals(this.parseQValue("0"), Some(QValue(0)))
	}

	test("QValue should parse 0.0") {
		assertEquals(this.parseQValue("0.0"), Some(QValue(0)))
	}

	test("QValue should parse 0.00") {
		assertEquals(this.parseQValue("0.00"), Some(QValue(0)))
	}

	test("QValue should parse 0.000") {
		assertEquals(this.parseQValue("0.000"), Some(QValue(0)))
	}

	test("QValue should parse 0.5") {
		assertEquals(this.parseQValue("0.5"), Some(QValue(500)))
	}

	test("QValue should parse 0.777") {
		assertEquals(this.parseQValue("0.777"), Some(QValue(777)))
	}

	test("QValue should not parse 0.0000") {
		assertEquals(this.parseQValue("0.0000"), None)
	}

	test("QValue should parse 1") {
		assertEquals(this.parseQValue("1"), Some(QValue(1000)))
	}

	test("QValue should parse 1.0") {
		assertEquals(this.parseQValue("1.0"), Some(QValue(1000)))
	}

	test("QValue should not parse 1.1") {
		assertEquals(this.parseQValue("1.1"), None)
	}

	test("QValue should roundtrip the complete range") {
		val orig	= 0 to 1000 map QValue.apply
		val trip	= orig map QValue.unparse map this.parseQValue
		assertEquals(trip, (orig map Some.apply))
	}
}
