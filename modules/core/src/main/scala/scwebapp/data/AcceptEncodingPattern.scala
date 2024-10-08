package scwebapp.data

import scutil.core.implicits.*

import scwebapp.format.*
import scparse.ng.text.*

object AcceptEncodingPattern {
	lazy val parser:TextParser[AcceptEncodingPattern]	= parsers.value

	def unparse(it:AcceptEncodingPattern):String	=
		it match {
			case Wildcard	=> "*"
			case Fixed(x)	=> AcceptEncodingType.unparse(x)
		}

	private object parsers {
		import HttpParsers.*

		val wildcard:TextParser[AcceptEncodingPattern]	=
			token.filter(_ == "*").named("wildcard \"*\"").tag(Wildcard)

		val fixed:TextParser[AcceptEncodingPattern]	=
			AcceptEncodingType.parser.map(Fixed.apply)

		val value:TextParser[AcceptEncodingPattern]	=
			wildcard.orElse(fixed)
	}
}

enum AcceptEncodingPattern {
	case Wildcard
	case Fixed(typ:AcceptEncodingType)

	// returns rank, if any
	def matches(typ:AcceptEncodingType):Option[Int]	=
		this matchOption {
			case AcceptEncodingPattern.Wildcard		=> 0
			case AcceptEncodingPattern.Fixed(`typ`)	=> 1
		}
}
