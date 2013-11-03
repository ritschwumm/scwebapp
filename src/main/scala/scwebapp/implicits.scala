package scwebapp

import scwebapp.pimp._

object implicits extends implicits		

trait implicits
		extends HttpServletRequestImplicits 
		with	HttpServletResponseImplicits
		with	HttpSessionImplicits
		with	ServletContextImplicits
		with	ServletConfigImplicits
		with	HttpResponderImplicits
