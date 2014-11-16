package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }

import javax.servlet._

object ServletConfigImplicits extends ServletConfigImplicits

trait ServletConfigImplicits {
	implicit def extendServletConfig(peer:ServletConfig):ServletConfigExtension		= 
			new ServletConfigExtension(peer)
}

final class ServletConfigExtension(peer:ServletConfig) {
	def initParameters:CaseParameters	=
			CaseParameters(
				for {
					name	<- (EnumerationUtil toIterator peer.getInitParameterNames.asInstanceOf[JEnumeration[String]]).toVector
				}
				yield name -> (peer getInitParameter name)
			)
}
