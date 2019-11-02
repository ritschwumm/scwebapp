package scwebapp.source

import scutil.lang._

import scwebapp.data._

object SourceCaching {
	case object			NotCached  						extends SourceCaching
	final case class	Expires(when:Endo[HttpDate])	extends SourceCaching
}

sealed trait SourceCaching
