package scwebapp

import javax.servlet.http._

final class HttpHandlerServlet(handler:HttpHandler) extends HttpServlet {
	override def service(request:HttpServletRequest, response:HttpServletResponse):Unit	=
			handler apply request apply response
}
