package scwebapp

import java.io.IOException

object HttpPartsProblem {
	final case class NotMultipart(e:Exception)					extends HttpPartsProblem
	final case class InputOutputFailed(e:IOException)			extends HttpPartsProblem
	final case class SizeLimitExceeded(e:IllegalStateException)	extends HttpPartsProblem
}

sealed trait HttpPartsProblem
