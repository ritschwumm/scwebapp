package scwebapp.servlet

import jakarta.servlet.http.*

import scutil.lang.Charsets

import scwebapp.*

object HttpHandlerServlet {
	val defaultEncoding	= Charsets.utf_8
}

final class HttpHandlerServlet(handler:HttpHandler) extends HttpServlet {
	override def service(request:HttpServletRequest, response:HttpServletResponse):Unit	= {
		if (request.getCharacterEncoding eq null) {
			request setCharacterEncoding HttpHandlerServlet.defaultEncoding.name
		}
		HttpIO.execute(request, response, handler)
	}
}
