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
	def initParameters:Parameters	=
			new Parameters {
				def caseSensitive:Boolean	= true
				
				def all:Seq[(String,String)]	=
						names map { _ firstBy peer.getInitParameter }
						
				def names:Seq[String]	=
						peer.getInitParameterNames.asInstanceOf[JEnumeration[String]].asScala.toVector
					
				def firstString(name:String):Option[String] =
						Option(peer getInitParameter name)
			}
}
