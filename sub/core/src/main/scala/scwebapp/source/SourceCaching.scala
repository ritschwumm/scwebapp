package scwebapp.source

import scutil.lang._

import scwebapp.data._

sealed trait SourceCaching
case object			SourceNotCached  					extends SourceCaching
final case class	SourceExpires(when:Endo[HttpDate])	extends SourceCaching
