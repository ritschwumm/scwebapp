package scwebapp
package pimp

import java.util.{  Enumeration=>JEnumeration, Set=>JSet }
import java.io.InputStream
import java.net.URL

import javax.servlet._

import scutil.implicits._

object ServletContextImplicits extends ServletContextImplicits

trait ServletContextImplicits {
	implicit def extendServletContext(peer:ServletContext):ServletContextExtension		=
			new ServletContextExtension(peer)
}

final class ServletContextExtension(peer:ServletContext) {
	def initParameters:CaseParameters	=
			CaseParameters(
				for {
					name	<- (EnumerationUtil toIterator peer.getInitParameterNames.asInstanceOf[JEnumeration[String]]).toVector
				}
				yield name -> (peer getInitParameter name)
			)
			
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
		
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
		 	new HttpAttribute[T](
					()	=> (peer getAttribute name).asInstanceOf[T],
					t	=> peer setAttribute (name, t),
					()	=> peer removeAttribute name)
}
