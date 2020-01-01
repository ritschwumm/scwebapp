package scwebapp.data

import scutil.base.implicits._

import scwebapp.format._
import scparse.ng.text._

object AcceptEncodingPattern {
	lazy val parser:TextParser[AcceptEncodingPattern]	= parsers.value

	def unparse(it:AcceptEncodingPattern):String	=
			it match {
				case Wildcard	=> "*"
				case Fixed(x)	=> AcceptEncodingType unparse x
			}

	private object parsers {
		import HttpParsers._

		val wildcard:TextParser[AcceptEncodingPattern]	=
				token ensure (_ == "*") named "wildcard \"*\"" tag Wildcard

		val fixed:TextParser[AcceptEncodingPattern]	=
				AcceptEncodingType.parser map Fixed.apply

		val value:TextParser[AcceptEncodingPattern]	=
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
