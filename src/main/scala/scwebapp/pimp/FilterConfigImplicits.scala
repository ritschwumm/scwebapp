package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }

import javax.servlet._

import scala.collection.JavaConverters._

import scutil.implicits._

object FilterConfigImplicits extends FilterConfigImplicits

trait FilterConfigImplicits {
	implicit def extendFilterConfig(peer:FilterConfig):FilterConfigExtension		= 
			new FilterConfigExtension(peer)
}

final class FilterConfigExtension(peer:FilterConfig) {
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
