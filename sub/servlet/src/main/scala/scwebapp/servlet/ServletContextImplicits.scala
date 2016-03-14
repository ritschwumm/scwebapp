package scwebapp.servlet

import java.util.{ Set=>JSet }

import javax.servlet._

import scutil.lang._
import scutil.implicits._
import scutil.io.ResourceProvider

import scwebapp._
import scwebapp.data._

object ServletContextImplicits extends ServletContextImplicits

trait ServletContextImplicits {
	implicit def extendServletContext(peer:ServletContext):ServletContextExtension		=
			new ServletContextExtension(peer)
}

final class ServletContextExtension(peer:ServletContext) {
	def mount(
		name:String,
		handler:HttpHandler,
		mappings:ISeq[String],
		loadOnStartup:Option[Int]
	):ServletRegistration.Dynamic	= {
		val servlet	= new HttpHandlerServlet(handler)
		val dynamic	= peer addServlet (name, servlet)
		dynamic addMapping (mappings:_*)
		loadOnStartup foreach dynamic.setLoadOnStartup
		dynamic	setAsyncSupported true
		dynamic
	}
	
	def mimeTypeFor(path:String):Option[MimeType]	=
			Option(peer getMimeType path) flatMap MimeType.parse
		
	def realPath(path:String):Option[String]	=
			Option(peer getRealPath path)
	
	def resources:ResourceProvider	=
			new ResourceProvider(path => Option(peer getResource path))
		
	def resourcePaths(base:String):Option[Set[String]]	=
			Option(peer getResourcePaths base) map { _.asInstanceOf[JSet[String]].toSet }
}
