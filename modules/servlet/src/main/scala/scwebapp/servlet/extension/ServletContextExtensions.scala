package scwebapp.servlet.extension

import java.util.{ Set as JSet }

import jakarta.servlet.*

import scutil.jdk.implicits.*
import scutil.classpath.*

import scwebapp.*
import scwebapp.data.*
import scwebapp.servlet.*

object ServletContextExtensions {
	extension(peer:ServletContext) {
		def mount(
			name:String,
			handler:HttpHandler,
			mappings:Seq[String],
			loadOnStartup:Option[Int],
			multipartConfig:Option[MultipartConfigElement]
		):ServletRegistration.Dynamic	= {
			val servlet	= new HttpHandlerServlet(handler)
			val dynamic	= peer.addServlet(name, servlet)
			dynamic.addMapping(mappings*)
			loadOnStartup	foreach dynamic.setLoadOnStartup
			multipartConfig	foreach dynamic.setMultipartConfig
			dynamic.setAsyncSupported(true)
			dynamic
		}

		def mimeTypeFor(path:String):Option[MimeType]	=
			Option(peer.getMimeType(path)).flatMap(MimeType.parse)

		def realPath(path:String):Option[String]	=
			Option(peer.getRealPath(path))

		def resource(path:String):Option[ClasspathResource]	=
			Option(peer.getResource(path)).map(new ClasspathResource(_))

		@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
		def resourcePaths(base:String):Option[Set[String]]	=
			Option(peer.getResourcePaths(base)).map(_.asInstanceOf[JSet[String]].toSet)

		def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			new HttpAttribute[T](
				()		=> peer.getAttribute(name),
				(it)	=> peer.setAttribute(name, it),
				()		=> peer.removeAttribute(name),
			)

		def initParameters:CaseParameters	=
			CaseParameters.extract(peer.getInitParameterNames, peer.getInitParameter)
	}
}
