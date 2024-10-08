package scwebapp.runner

import java.io.Writer
import jakarta.servlet.http.HttpServletRequest
import org.eclipse.jetty.server.handler.ErrorHandler

import scutil.core.implicits.*
import scutil.log.*

final class LogErrorHandler extends ErrorHandler with Logging {
	//@throws(classOf[IOException])
	@SuppressWarnings(Array("org.wartremover.warts.ToString"))
	override def handleErrorPage(request:HttpServletRequest, writer:Writer, code:Int, message:String):Unit = {
		WARN.log(
			LogValue.string("handling error") +:
			Vector[Option[LogValue]](
				Option(message).map(LogValue.string),
				Option(request.getAttribute("javax.servlet.error.exception")).map {
					case e:Throwable	=> LogValue.throwable(e)
					case x				=> LogValue.string(x.toString)
				},
				Option(request.getAttribute("javax.servlet.error.request_uri"))	.map(_.toString).map(LogValue.string),
				Option(request.getAttribute("javax.servlet.error.servlet_name")).map(_.toString).map(LogValue.string)
			).flattenOption
		)
		writer.write(code.toString)
	}
}
