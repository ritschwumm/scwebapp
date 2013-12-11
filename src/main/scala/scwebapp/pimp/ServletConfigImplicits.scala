package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }

import javax.servlet._

import scala.collection.JavaConverters._

import scutil.implicits._

object ServletConfigImplicits extends ServletConfigImplicits

trait ServletConfigImplicits {
	implicit def extendServletConfig(peer:ServletConfig):ServletConfigExtension		= 
			new ServletConfigExtension(peer)
}

final class ServletConfigExtension(peer:ServletConfig) {
	def initParameters:CaseParameters	=
			CaseParameters(
				for {
					name	<- peer.getInitParameterNames.asInstanceOf[JEnumeration[String]].asScala.toVector
				}
				yield name -> (peer getInitParameter name)
			)
}
