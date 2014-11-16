package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }

import javax.servlet._

object FilterConfigImplicits extends FilterConfigImplicits

trait FilterConfigImplicits {
	implicit def extendFilterConfig(peer:FilterConfig):FilterConfigExtension		= 
			new FilterConfigExtension(peer)
}

final class FilterConfigExtension(peer:FilterConfig) {
	def initParameters:CaseParameters	=
			CaseParameters(
				for {
					name	<- (EnumerationUtil toIterator peer.getInitParameterNames.asInstanceOf[JEnumeration[String]]).toVector
				}
				yield name	-> (peer getInitParameter name)
			)
}
