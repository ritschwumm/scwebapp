package scwebapp.runner

import javax.servlet.http._

import org.eclipse.jetty.server._
import org.eclipse.jetty.server.handler.AbstractHandler

import scutil.lang._
import scutil.lang.implicits._
import scutil.log._

import scwebapp.HttpHandler
import scwebapp.servlet.HttpIO

object RunnerBase extends Logging {
	def start(config:ServerConfig, application:Using[HttpHandler]):Unit	= {
		// starts with /
		// does not end with /
		// is "" for the root context
		val contextPath	= config.path.value.replaceAll("/$", "")

		val (httpHandler, disposer)	=
			try {
				INFO(s"starting application")
				application.open()
			}
			catch { case e:Exception	=>
				ERROR("cannot start application", e)
				sys exit 1
			}

		val handler		=
			new AbstractHandler {
				// throws IOException, ServletException
				def handle(target:String, baseRequest:Request, request:HttpServletRequest, response:HttpServletResponse):Unit	= {
					if (target startsWith (contextPath + "/")) {
						baseRequest setContextPath contextPath
						HttpIO.execute(request, response, httpHandler)
						baseRequest setHandled true
					}
					else {
						// TODO what if not?
						ERROR("unexpected request", target)
					}
				}
			}

		val server	= new Server(config.bindAdr)

		val httpConfig		= new HttpConfiguration
		httpConfig	setSendServerVersion	false
		httpConfig	setSendXPoweredBy		false
		httpConfig	setSendDateHeader		false

		val	httpFactory		= new HttpConnectionFactory(httpConfig)

		val	httpConnector	= new ServerConnector(server, httpFactory)
		httpConnector	setHost			config.bindAdr.getHostName
		httpConnector	setPort			config.bindAdr.getPort
		httpConnector	setIdleTimeout	config.idleTimeout.millis

		server	setConnectors		Array(httpConnector)
		server	setHandler			handler
		server	setStopAtShutdown	true
		server	setStopTimeout		7000

		// handles 404 outside the webapp's context
		val errorHandler	= new LogErrorHandler
		errorHandler	setServer	server
		server			addBean		errorHandler

		INFO("starting server")
		@volatile var running:Boolean	= true
		try {
			server.start()
		}
		catch { case e:Exception	=>
			ERROR("cannot start server", e)
			sys exit 1
		}

		def stop():Unit	= {
			if (running) {
				running = false

				INFO("stopping server")
				server.stop()
				disposer.dispose()
			}
		}

		Runtime.getRuntime addShutdownHook new Thread(() => stop())

		val url	= show"http://${config.host.cata("localhost", _.toString)}:${config.port.value.toString}${config.path.value}"
		INFO(show"point your browser to $url")

		INFO("press enter to stop")
		val read	= System.in.read()
		if (read != -1) {
			stop()
		}

		server.join()
	}
}
