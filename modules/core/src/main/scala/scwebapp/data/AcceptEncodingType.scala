package scwebapp.data

import scwebapp.format._
import scparse.ng.text._

object AcceptEncodingType {
	lazy val parser:TextParser[AcceptEncodingType]	= parsers.value

	def unparse(it:AcceptEncodingType):String	=
		it match {
			case Identity	=> "identity"
			case Other(x)	=> ContentEncodingType unparse x
		}

	private object parsers {
		import HttpParsers._

		val identity:TextParser[AcceptEncodingType]	=
			token map CaseUtil.lowerCase filter (_ == "identity") named "identity \"identity\"" tag Identity

		val other:TextParser[AcceptEncodingType]	=
			ContentEncodingType.parser map Other.apply

		val value:TextParser[AcceptEncodingType]	=
			identity orElse other
	}

	//------------------------------------------------------------------------------

	case object Identity								extends AcceptEncodingType
	final case class  Other(typ:ContentEncodingType)	extends AcceptEncodingType

}

sealed trait AcceptEncodingType
