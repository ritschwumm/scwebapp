package scwebapp.parser.string

import scwebapp.parser._

final case class StringInput(s:String, i:Int) extends Input[Char] {
	def next:Option[(Input[Char],Char)]	=
			if (i < s.length)	Some(((StringInput(s, i+1)), (s charAt i)))
			else				None
}
