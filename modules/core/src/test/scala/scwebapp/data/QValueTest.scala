package scwebapp.data

import org.specs2.mutable._

import scwebapp.format._
import scparse.ng.text._

class QValueTest extends Specification {
	val parser	= QValue.parser finish HttpParsers.LWSP
	def parseQValue(it:String):Option[QValue] = parser.parseString(it).toOption

	"QValue" should {
		"parse 0" in {
			this parseQValue "0" mustEqual
			Some(QValue(0))
		}
		"parse 0.0" in {
			this parseQValue "0.0" mustEqual
			Some(QValue(0))
		}
		"parse 0.00" in {
			this parseQValue "0.00" mustEqual
			Some(QValue(0))
		}
		"parse 0.000" in {
			this parseQValue "0.000" mustEqual
			Some(QValue(0))
		}
		"parse 0.5" in {
			this parseQValue "0.5" mustEqual
			Some(QValue(500))
		}
		"parse 0.777" in {
			this parseQValue "0.777" mustEqual
			Some(QValue(777))
		}
		"not parse 0.0000" in {
			this parseQValue "0.0000" mustEqual
			None
		}
		"parse 1" in {
			this parseQValue "1" mustEqual
			Some(QValue(1000))
		}
		"parse 1.0" in {
			this parseQValue "1.0" mustEqual
			Some(QValue(1000))
		}
		"not parse 1.1" in {
			this parseQValue "1.1" mustEqual
			None
		}
		"roundtrip the complete range" in {
			val orig	= 0 to 1000 map QValue.apply
			val trip	= orig map QValue.unparse map this.parseQValue
			trip mustEqual (orig map Some.apply)
		}
	}
}
