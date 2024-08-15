package scwebapp.data

import scutil.core.implicits.*

import scwebapp.format.*
import scparse.ng.text.*

object QValue {
	val zero	= QValue(0)
	val one		= QValue(1000)

	//------------------------------------------------------------------------------

	lazy val parser:TextParser[QValue]	= parsers.value

	def unparse(it:QValue):String	= {
		val raw	= it.promille.toString.reverse.padTo(4, '0').dropWhile(_ == '0').reverse
		raw.length match {
			case 0	=> "0"
			case 1	=> raw
			case _	=> raw.patch(1, ".", 0)
		}
	}

	private object parsers {
		import HttpParsers.*

		lazy val value:TextParser[QValue]	=
			low.orElse(high).map { (h,l) =>
				QValue(digitVal(h) * 1000 + tailing(l.flattenMany map digitVal, 100))
			}

		lazy val finished:TextParser[QValue]	=
			value.phrase

		lazy val low:TextParser[(Char,Option[Seq[Char]])]	=
			TextParser.is('0').next(
				TextParser.is('.').right(DIGIT.timesUpTo(3)).option
			)
		lazy val high:TextParser[(Char,Option[Seq[Char]])]	=
			TextParser.is('1').next(
				TextParser.is('.').right(TextParser.is('0').timesUpTo(3)).option
			)

		private def tailing(digits:Seq[Int], factor:Int):Int =
			if (factor == 0)	0
			else {
				digits match {
					case head +: tail	=> head * factor + tailing(tail, factor / 10)
					case _				=> 0
				}
			}

		private def digitVal(c:Char):Int	= c - '0'
	}
}

final case class QValue(promille:Int) extends Ordered[QValue] {
	require(promille >= 0,		"must be at least 0")
	require(promille <= 1000,	"must at max be 1000")

	def compare(that:QValue):Int	= this.promille compare that.promille

	override def toString():String	=
		QValue.unparse(this)
}
