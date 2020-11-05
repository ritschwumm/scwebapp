package scwebapp.source

import scwebapp.data._

object SourceCaching {
	case object			Silent								extends SourceCaching
	case object			Disabled  							extends SourceCaching
	final case class	Expires(when:HttpDate=>HttpDate)	extends SourceCaching
}

sealed trait SourceCaching
