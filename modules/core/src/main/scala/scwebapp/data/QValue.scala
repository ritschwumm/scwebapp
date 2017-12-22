package scwebapp.data

import scutil.base.implicits._
import scutil.core.implicits._
import scutil.lang._

import scwebapp.format._
import scwebapp.parser.string._

object QValue {
	val zero	= QValue(0)
	val one		= QValue(1000)
	
	//------------------------------------------------------------------------------
	
	lazy val parser:CParser[QValue]	= parsers.value
	
	def unparse(it:QValue):String	= {
		val raw	= it.promille.toString.reverse.padTo(4, '0').dropWhile(_ == '0').reverse
		raw.length match {
			case 0	=> "0"
			case 1	=> raw
			case _	=> raw patch (1, ".", 0)
		}
	}
	
	private object parsers {
		import HttpParsers._
		
		lazy val value:CParser[QValue]	=
				low orElse high map { case (h,l) =>
					QValue(digitVal(h) * 1000 + tail(l.flattenMany map digitVal, 100))
				}
				
		lazy val finished:CParser[QValue]	=
				value.phrase

		lazy val low:CParser[(Char,Option[ISeq[Char]])]		= cis('0') next (cis('.') right (DIGIT		upto 3)).option
		lazy val high:CParser[(Char,Option[ISeq[Char]])]	= cis('1') next (cis('.') right (cis('0')	upto 3)).option
		
		private def tail(digits:ISeq[Int], factor:Int):Int =
				if (digits.isEmpty || factor == 0)	0
				else digits.head * factor + tail(digits.tail, factor / 10)

		private def digitVal(c:Char):Int	= c - '0'
	}
}

final case class QValue(promille:Int) extends Ordered[QValue] {
	require(promille >= 0,		"must be at least 0")
	require(promille <= 1000,	"must at max be 1000")
	
	def compare(that:QValue):Int	= this.promille compare that.promille
	
	override def toString():String	=
			QValue unparse this
}
