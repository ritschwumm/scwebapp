package scwebapp.runner

import javax.servlet.http._

import org.eclipse.jetty.server._
import org.eclipse.jetty.server.handler.AbstractHandler

import scutil.lang._
import scutil.log._

import scwebapp.HttpHandler
import scwebapp.servlet.HttpIO

object RunnerBase extends Logging {
	def start(config:ServerConfig, application:() => (Disposable, HttpHandler)) {
		// starts with /
		// does not end with /
		// is "" for the root context
		val contextPath	= config.path.value replaceAll ("/$", "")
		
		val (disposable, httpHandler)	= 
				try {
					INFO("starting server")
					application()
				}
				catch { case e:Exception	=>
					ERROR("cannot start server", e)
					sys exit 1
				}
		
		val handler		= new AbstractHandler {
			// throws IOException, ServletException
			def handle(target:String, baseRequest:Request, request:HttpServletRequest, response:HttpServletResponse) {
				if (target startsWith (contextPath + "/")) {
					baseRequest setContextPath contextPath
					HttpIO execute (request, response, httpHandler)
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
		httpConnector	setHost	config.bindAdr.getHostName
		httpConnector	setPort	config.bindAdr.getPort
		
		server	setConnectors		Array(httpConnector)
		server	setHandler			handler
		server	setStopAtShutdown	true
		server	setStopTimeout		7000
		
		// handles 404 outside the webapp's context
		val errorHandler	= new LogErrorHandler
		errorHandler	setServer	server
		server			addBean		errorHandler
		
		INFO("starting server")
		try {
			server.start()
		}
		catch { case e:Exception	=>
			ERROR("cannot start server", e)
			sys exit 1
		}
		
		INFO("press enter to stop")
		val read	= System.in.read()
		if (read != -1) {
			INFO("stopping server")
			server.stop()
			disposable.dispose()
		}
		
		server.join()
	}
}
