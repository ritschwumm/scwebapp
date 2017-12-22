package scwebapp

import java.io.IOException

sealed trait HttpPartsProblem
final case class NotMultipart(e:Exception)					extends HttpPartsProblem
final case class InputOutputFailed(e:IOException)				extends HttpPartsProblem
final case class SizeLimitExceeded(e:IllegalStateException)	extends HttpPartsProblem
