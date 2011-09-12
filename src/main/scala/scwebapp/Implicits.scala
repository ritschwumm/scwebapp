package scwebapp

object HttpImplicits extends HttpImplicits
trait HttpImplicits 
		extends ServletApiImplicits
		with 	ScWebappImplicits

object ServletApiImplicits extends ServletApiImplicits		
trait ServletApiImplicits
		extends HttpServletRequestImplicits 
		with	HttpServletResponseImplicits
		with	HttpSessionImplicits
		with	ServletContextImplicits
		with	ServletConfigImplicits
	
object ScWebappImplicits extends ScWebappImplicits
trait ScWebappImplicits
		extends	HttpHandlerImplicits
		with	HttpChanceImplicits
		with	HttpRouteImplicits
		with	HttpResponderImplicits
