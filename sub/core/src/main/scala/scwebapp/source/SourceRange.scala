package scwebapp.source

import java.io.OutputStream

import scutil.lang._

object SourceRange {
	def apply(func:Effect[OutputStream]):SourceRange	=
		new SimpleSourceRange(func)
}
trait SourceRange {
	def transferTo(output:OutputStream):Unit
}

private final class SimpleSourceRange(effect:Effect[OutputStream]) extends SourceRange {
	def transferTo(output:OutputStream):Unit	= effect(output)
}
