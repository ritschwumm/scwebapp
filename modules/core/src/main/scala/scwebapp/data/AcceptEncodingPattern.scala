package scwebapp.data

import scutil.base.implicits._

import scwebapp.format._
import scwebapp.parser.string._

object AcceptEncodingPattern {
	lazy val parser:CParser[AcceptEncodingPattern]	= parsers.value

	def unparse(it:AcceptEncodingPattern):String	=
			it match {
				case Wildcard	=> "*"
				case Fixed(x)	=> AcceptEncodingType unparse x
			}

	private object parsers {
		import HttpParsers._

		val wildcard:CParser[AcceptEncodingPattern]	=
				token filter (_ == "*") tag Wildcard

		val fixed:CParser[AcceptEncodingPattern]	=
				AcceptEncodingType.parser map Fixed.apply

		val value:CParser[AcceptEncodingPattern]	=
				wildcard orElse fixed
	}

	//------------------------------------------------------------------------------

	final case object Wildcard						extends AcceptEncodingPattern
	final case class  Fixed(typ:AcceptEncodingType)	extends AcceptEncodingPattern

}

sealed trait AcceptEncodingPattern {
	// returns rank, if any
	def matches(typ:AcceptEncodingType):Option[Int]	=
			this matchOption {
				case AcceptEncodingPattern.Wildcard		=> 0
				case AcceptEncodingPattern.Fixed(`typ`)	=> 1
			}
}
