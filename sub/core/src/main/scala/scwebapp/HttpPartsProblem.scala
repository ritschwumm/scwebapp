package scwebapp

import java.io.IOException

sealed trait HttpPartsProblem
case class NotMultipart(e:Exception)					extends HttpPartsProblem
case class InputOutputFailed(e:IOException)				extends HttpPartsProblem
case class SizeLimitExceeded(e:IllegalStateException)	extends HttpPartsProblem
