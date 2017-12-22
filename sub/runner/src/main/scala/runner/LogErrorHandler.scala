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
			StringAsLogValue("handling error") +:
			Vector(
				Option(message) map StringAsLogValue,
				Option(request getAttribute "javax.servlet.error.exception") map {
					case e:Throwable	=> ThrowableAsLogValue(e)
					case x				=> StringAsLogValue(x.toString)
				},
				Option(request getAttribute "javax.servlet.error.request_uri")	map (_.toString) map StringAsLogValue,
				Option(request getAttribute "javax.servlet.error.servlet_name")	map (_.toString) map StringAsLogValue
			).collapse
		)
		writer write code.toString
	}
}
