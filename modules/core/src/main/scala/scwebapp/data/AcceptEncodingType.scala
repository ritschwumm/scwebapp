package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object AcceptEncodingType {
	lazy val parser:CParser[AcceptEncodingType]	= parsers.value

	def unparse(it:AcceptEncodingType):String	=
			it match {
				case Identity	=> "identity"
				case Other(x)	=> ContentEncodingType unparse x
			}

	private object parsers {
		import HttpParsers._

		val identity:CParser[AcceptEncodingType]	=
				token map CaseUtil.lowerCase  filter (_ == "identity") tag Identity

		val other:CParser[AcceptEncodingType]	=
				ContentEncodingType.parser map Other.apply

		val value:CParser[AcceptEncodingType]	=
				identity orElse other
	}

	//------------------------------------------------------------------------------

	final case object Identity							extends AcceptEncodingType
	final case class  Other(typ:ContentEncodingType)	extends AcceptEncodingType

}

sealed trait AcceptEncodingType
