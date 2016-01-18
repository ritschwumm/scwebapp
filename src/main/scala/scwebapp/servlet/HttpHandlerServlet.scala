package scwebapp.servlet

import javax.servlet.http._

import scutil.io.Charsets

import scwebapp.HttpHandler

object HttpHandlerServlet {
	val defaultEncoding	= Charsets.utf_8
}

final class HttpHandlerServlet(handler:HttpHandler) extends HttpServlet {
	override def service(request:HttpServletRequest, response:HttpServletResponse) {
		if (request.getCharacterEncoding eq null) {
			request setCharacterEncoding HttpHandlerServlet.defaultEncoding.name
		}
		handler apply request apply response
	}
}
