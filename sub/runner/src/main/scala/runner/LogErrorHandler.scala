package scwebapp.runner

import java.io.Writer
import javax.servlet.http.HttpServletRequest
import org.eclipse.jetty.server.handler.ErrorHandler

import scutil.log._

final class LogErrorHandler extends ErrorHandler with Logging {
	//@throws(classOf[IOException])
	override def handleErrorPage(request:HttpServletRequest, writer:Writer, code:Int, message:String):Unit = {
		val messageOpt		= Option(message)
		val exceptionOpt	= Option(request getAttribute "javax.servlet.error.exception")
		val requestUri		= request getAttribute "javax.servlet.error.request_uri"
		val servletNameOpt	= Option(request getAttribute "javax.servlet.error.servlet_name")
		WARN("handling error", code, messageOpt.orNull, exceptionOpt.orNull, requestUri, servletNameOpt.orNull)
		writer write code.toString
	}
}
