package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }

import javax.servlet._

import scutil.implicits._

object FilterConfigImplicits extends FilterConfigImplicits

trait FilterConfigImplicits {
	implicit def extendFilterConfig(peer:FilterConfig):FilterConfigExtension		=
			new FilterConfigExtension(peer)
}

final class FilterConfigExtension(peer:FilterConfig) {
	def initParameters:CaseParameters	=
			CaseParameters(
				for {
					name	<- peer.getInitParameterNames.asInstanceOf[JEnumeration[String]].toIterator.toVector
				}
				yield name	-> (peer getInitParameter name)
			)
}
