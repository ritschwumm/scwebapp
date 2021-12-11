package scwebapp.servlet.bootstrap

import jakarta.servlet._

import scutil.lang._

import scwebapp._
import scwebapp.servlet.extensions._

/** make an object extending this and annotate it with javax.servlet.annotation.WebListener */
trait HttpHandlerServletContextListener extends BootstrapServletContextListener {
	protected final def application(sc:ServletContext):IoResource[Unit]	=
		for {
			handler	<-	httpHandler(sc.initParameters firstString _)
			_		<-	IoResource delay {
							sc.mount(
								name			= "HttpHandlerServlet",
								handler			= handler,
								mappings		= Vector("/*"),
								loadOnStartup	= Some(100),
								multipartConfig	= None
							)
						}
		}
		yield ()

	protected def httpHandler(props:String=>Option[String]):IoResource[HttpHandler]
}
