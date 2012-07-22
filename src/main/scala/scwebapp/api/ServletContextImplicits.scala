package scwebapp
package api

import java.util.{ Set=>JSet }
import java.io.InputStream
import java.net.URL
import javax.servlet._

import scala.collection.JavaConverters._

object ServletContextImplicits extends ServletContextImplicits

trait ServletContextImplicits {
	implicit def extendServletContext(delegate:ServletContext):ServletContextExtension		= 
			new ServletContextExtension(delegate)
}

final class ServletContextExtension(delegate:ServletContext) {
	def mimeTypeFor(path:String):Option[MimeType]	=
			Option(delegate getMimeType path) flatMap MimeType.parse 
	
	def resourceOption(path:String):Option[URL]	=
			Option(delegate getResource path)

	def resourceAsStreamOption(path:String):Option[InputStream]	=
			Option(delegate getResourceAsStream path)
			
	def resourcePaths(base:String):Option[Set[String]]	=
			Option(delegate getResourcePaths base) map { _.asInstanceOf[JSet[String]].asScala.toSet }
		
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
		 	new HttpAttribute[T](
					()	=> (delegate getAttribute name).asInstanceOf[T],
					t	=> delegate setAttribute (name, t),
					()	=> delegate removeAttribute name)
}