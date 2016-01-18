package scwebapp
package servlet

import java.io._

import javax.servlet._
import javax.servlet.http._

import scutil.implicits._

import scwebapp.handler._
import scwebapp.servlet.implicits._

final class FileServlet extends HttpServlet {
	private var handler:FileHandler	= null

	@throws(classOf[ServletException])
	override  def init() {
		val baseParam	=
				getServletConfig.initParameters firstString "basePath" getOrElse {
					throw new ServletException("missing init param basePath")
				}
		val basePath	=
				getServletContext realPath baseParam getOrElse {
					throw new ServletException("missing realpath for init param basePath")
				}
			
		val baseDir	= new File(basePath)
		if (!(baseDir.exists && baseDir.isDirectory && baseDir.canRead))
				throw new ServletException(so"FileServlet init param basePath's value '${basePath}' does not specify an existing, readable directory.")
			
		handler	= new FileHandler(baseDir)
	}

	override protected def service(request:HttpServletRequest, response:HttpServletResponse) {
		HttpIO execute (request, response, handler)
	}
}
