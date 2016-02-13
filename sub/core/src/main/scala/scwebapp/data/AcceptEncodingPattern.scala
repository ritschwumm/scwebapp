package scwebapp.data

import scutil.implicits._

import scwebapp.format._
import scwebapp.parser.string._

object AcceptEncodingPattern {
	lazy val parser:CParser[AcceptEncodingPattern]	= parsers.value
		
	def unparse(it:AcceptEncodingPattern):String	=
			it match {
				case AcceptEncodingWildcard	=> "*"
				case AcceptEncodingFixed(x)	=> AcceptEncodingType unparse x
			}
			
	private object parsers {
		import HttpParsers._
		
		val wildcard:CParser[AcceptEncodingPattern]	=
				token filter (_ == "*") tag AcceptEncodingWildcard
			
		val fixed:CParser[AcceptEncodingPattern]	=
				AcceptEncodingType.parser map AcceptEncodingFixed.apply
			
		val value:CParser[AcceptEncodingPattern]	=
				wildcard orElse fixed
	}
}

sealed trait AcceptEncodingPattern {
	// returns rank, if any
	def matches(typ:AcceptEncodingType):Option[Int]	=
			this matchOption {
				case AcceptEncodingWildcard		=> 0
				case AcceptEncodingFixed(`typ`)	=> 1
			}
}
case object AcceptEncodingWildcard						extends AcceptEncodingPattern
case class  AcceptEncodingFixed(typ:AcceptEncodingType)	extends AcceptEncodingPattern
