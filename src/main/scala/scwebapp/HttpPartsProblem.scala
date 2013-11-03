package scwebapp

import java.io.IOException

import javax.servlet.ServletException

sealed trait HttpPartsProblem
case class NotMultipart(e:ServletException)				extends HttpPartsProblem
case class InputOutputFailed(e:IOException)				extends HttpPartsProblem
case class SizeLimitExceeded(e:IllegalStateException)	extends HttpPartsProblem
