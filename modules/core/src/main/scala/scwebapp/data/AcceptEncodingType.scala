package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object AcceptEncodingType {
	lazy val parser:CParser[AcceptEncodingType]	= parsers.value

	def unparse(it:AcceptEncodingType):String	=
			it match {
				case AcceptEncodingIdentity	=> "identity"
				case AcceptEncodingOther(x)	=> ContentEncodingType unparse x
			}

	private object parsers {
		import HttpParsers._

		val identity:CParser[AcceptEncodingType]	=
				token map CaseUtil.lowerCase  filter (_ == "identity") tag AcceptEncodingIdentity

		val other:CParser[AcceptEncodingType]	=
				ContentEncodingType.parser map AcceptEncodingOther.apply

		val value:CParser[AcceptEncodingType]	=
				identity orElse other
	}
}

sealed trait AcceptEncodingType
		case object AcceptEncodingIdentity							extends AcceptEncodingType
final	case class  AcceptEncodingOther(typ:ContentEncodingType)	extends AcceptEncodingType
