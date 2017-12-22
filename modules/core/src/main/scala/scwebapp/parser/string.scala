package scwebapp.parser

import scutil.lang._

import Parser._

package object string {
	type CParser[T]	= Parser[Char,T]
	
	def cis(c:Char):CParser[Char]		= is(c)
	def sis(c:String):CParser[String]	= iss(c.toVector) map { _.mkString }
	
	def rng(from:Char, to:Char):CParser[Char]	=
			any[Char] filter { c => c >= from && c <= to }
		
	def in(s:String):CParser[Char]	=
			any[Char] filter { s contains _ }
		
	//------------------------------------------------------------------------------
	
	def stringInput(s:String):Input[Char]	=  StringInput(s, 0)
	
	//------------------------------------------------------------------------------

	implicit class RichStringNest[S](peer:Parser[S,String]) {
		def nestString[T](inner:Parser[Char,T]):Parser[S,T]	=
				peer nest (stringInput, inner)	
	}
	
	implicit class RichCharParser[T](peer:Parser[Char,T]) {
		def parseStringOption(s:String):Option[T]	=
				parseString(s).toOption
			
		def parseString(s:String):Result[Char,T]	=
				peer parse stringInput(s)
	}
	
	implicit class RichCharISeqParser[T](peer:Parser[T,ISeq[Char]]) {
		def stringify:Parser[T,String]	=
				peer map { _.mkString }
	}
	
	implicit class RichCharNesParser[T](peer:Parser[T,Nes[Char]]) {
		def stringify:Parser[T,String]	=
				peer map { _.toISeq.mkString }
	}
}
