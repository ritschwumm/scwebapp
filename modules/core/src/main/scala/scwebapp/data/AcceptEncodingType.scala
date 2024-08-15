package scwebapp.data

import scwebapp.format.*
import scparse.ng.text.*

object AcceptEncodingType {
	lazy val parser:TextParser[AcceptEncodingType]	= parsers.value

	def unparse(it:AcceptEncodingType):String	=
		it match {
			case Identity	=> "identity"
			case Other(x)	=> ContentEncodingType.unparse(x)
		}

	private object parsers {
		import HttpParsers.*

		val identity:TextParser[AcceptEncodingType]	=
			token.map(CaseUtil.lowerCase).filter(_ == "identity").named("identity \"identity\"").tag(Identity)

		val other:TextParser[AcceptEncodingType]	=
			ContentEncodingType.parser.map(Other.apply)

		val value:TextParser[AcceptEncodingType]	=
			identity.orElse(other)
	}
}

enum AcceptEncodingType {
	case Identity
	case Other(typ:ContentEncodingType)
}
