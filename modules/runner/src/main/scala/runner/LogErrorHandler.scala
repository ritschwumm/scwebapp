package scwebapp.runner

import java.io.Writer
import javax.servlet.http.HttpServletRequest
import org.eclipse.jetty.server.handler.ErrorHandler

import scutil.base.implicits._
import scutil.log._

final class LogErrorHandler extends ErrorHandler with Logging {
	//@throws(classOf[IOException])
	override def handleErrorPage(request:HttpServletRequest, writer:Writer, code:Int, message:String):Unit = {
		WARN log (
			LogString("handling error") +:
			Vector(
				Option(message) map LogString.apply,
				Option(request getAttribute "javax.servlet.error.exception") map {
					case e:Throwable	=> LogThrowable(e)
					case x				=> LogString(x.toString)
				},
				Option(request getAttribute "javax.servlet.error.request_uri")	map (_.toString) map LogString.apply,
				Option(request getAttribute "javax.servlet.error.servlet_name")	map (_.toString) map LogString.apply
			).collapse
		)
		writer write code.toString
	}
}
