package scwebapp
package pimp

import java.util.{  Enumeration=>JEnumeration, Set=>JSet }
import java.io.InputStream
import java.net.URL

import javax.servlet._

import scutil.lang._
import scutil.implicits._

import scwebapp.servlet.HttpHandlerServlet

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
		loadOnStartup:Option[Int],
		asyncSupported:Boolean
	):ServletRegistration.Dynamic	= {
		val servlet	= new HttpHandlerServlet(handler)
		val dynamic	= peer addServlet (name, servlet)
		dynamic addMapping (mappings:_*)
		loadOnStartup foreach dynamic.setLoadOnStartup
		dynamic	setAsyncSupported asyncSupported
		dynamic
	}
	
	def mimeTypeFor(path:String):Option[MimeType]	=
			Option(peer getMimeType path) flatMap MimeType.parse
		
	def realPath(path:String):Option[String]	=
			Option(peer getRealPath path)
	
	def resourceOption(path:String):Option[URL]	=
			Option(peer getResource path)

	def resourceAsStreamOption(path:String):Option[InputStream]	=
			Option(peer getResourceAsStream path)
			
	def resourcePaths(base:String):Option[Set[String]]	=
			Option(peer getResourcePaths base) map { _.asInstanceOf[JSet[String]].toSet }
}
