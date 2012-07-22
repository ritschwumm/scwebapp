package scwebapp
package api

object ApiImplicits extends ApiImplicits		

trait ApiImplicits
		extends HttpServletRequestImplicits 
		with	HttpServletResponseImplicits
		with	HttpSessionImplicits
		with	ServletContextImplicits
		with	ServletConfigImplicits
