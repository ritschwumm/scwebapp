package scwebapp
package syntax

object SyntaxImplicits extends SyntaxImplicits

trait SyntaxImplicits
		extends	HttpHandlerImplicits
		with	HttpPHandlerImplicits
		with	HttpResponderImplicits
