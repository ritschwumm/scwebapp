package scwebapp.source

import java.io.OutputStream

trait SourceRange {
	def transferTo(output:OutputStream):Unit
}
