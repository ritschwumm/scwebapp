package scwebapp.servlet.bootstrap

import jakarta.servlet._

import scutil.lang._

/** make an object extending this and annotate it with javax.servlet.annotation.WebListener */
trait BootstrapServletContextListener extends ServletContextListener{
	@volatile private var disposer:Io[Unit]	= Io.unit

	override final def contextInitialized(ev:ServletContextEvent):Unit	= {
		disposer	= application(ev.getServletContext).openVoid.unsafeRun()
	}

	override final def contextDestroyed(ev:ServletContextEvent):Unit	= {
		disposer.unsafeRun()
	}

	protected def application(sc:ServletContext):IoResource[Unit]
}
