package scwebapp.source

import scutil.lang._

import scwebapp.data._

object SourceCaching {
	case object			Silent							extends SourceCaching
	case object			Disabled  						extends SourceCaching
	final case class	Expires(when:Endo[HttpDate])	extends SourceCaching

	@deprecated("use Disabled", "0.200.0")
	val NotCached	= Disabled
}

sealed trait SourceCaching
