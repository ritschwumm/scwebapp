package scwebapp

import scwebapp.pimp._

object implicits extends implicits		

trait implicits
		extends HasInitParametersImplicits
		with	HasAttributesImplicits
		with	HttpServletRequestImplicits
		with	HttpServletResponseImplicits
		with	ServletContextImplicits
		with	HttpResponderImplicits
		with	PartImplicits
