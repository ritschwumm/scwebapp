package scwebapp.servlet.bootstrap

import javax.servlet._

import scutil.lang._
import scutil.log._

import scwebapp._
import scwebapp.servlet.implicits._

/** make an object extending this and annotate it with javax.servlet.annotation.WebListener */
trait BootstrapBase extends ServletContextListener with Logging {
	@volatile private var disposer:Option[Disposer]	= None

	def contextInitialized(ev:ServletContextEvent):Unit	= {
		val sc	= ev.getServletContext

		INFO("starting application")
		val (handler,tmp)	= startup(sc.initParameters firstString _).open()
		disposer			= Some(tmp)

		INFO("creating web servlet")
		sc.mount(
			name			= "WebServlet",
			handler			= handler,
			mappings		= Vector("/*"),
			loadOnStartup	= Some(100)
		)
	}

	def contextDestroyed(ev:ServletContextEvent):Unit	= {
		INFO("stopping application")
		disposer	foreach { _.dispose() }
	}

	protected def startup(props:String=>Option[String]):Using[HttpHandler]
}
