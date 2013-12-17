package scwebapp.parser

import Parser._

package object string {
	type CParser[T]	= Parser[Char,T]
	
	def cis(c:Char):CParser[Char]	= is(c)
	
	def rng(from:Char, to:Char):CParser[Char]	=
			any[Char] filter { c => c >= from && c <= to }
		
	def in(s:String):CParser[Char]	=
			any[Char] filter { s contains _ }
		
	//------------------------------------------------------------------------------
	
	def stringInput(s:String):Input[Char]	=  StringInput(s, 0)
	
	case class StringInput(s:String, i:Int) extends Input[Char] {
		def next:Option[(Input[Char],Char)]	=
				if (i < s.length)	Some((StringInput(s, i+1)), s charAt i)
				else				None
	}
	
	//------------------------------------------------------------------------------

	implicit class RichCharParser[T](peer:Parser[Char,T]) {
		def parseStringOption(s:String):Option[T]	=
				peer parse stringInput(s) toOption;
	}
}
