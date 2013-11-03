package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }
import javax.servlet._

import scala.collection.JavaConverters._

import scutil.Implicits._

object ServletConfigImplicits extends ServletConfigImplicits

trait ServletConfigImplicits {
	implicit def extendServletConfig(peer:ServletConfig):ServletConfigExtension		= 
			new ServletConfigExtension(peer)
}

final class ServletConfigExtension(peer:ServletConfig) {
	def initParameters:Seq[(String,String)]	=
			initParameterNames map { _ firstBy peer.getInitParameter }
			
	def initParameterNames:Seq[String]	=
			peer.getInitParameterNames.asInstanceOf[JEnumeration[String]].asScala.toVector
			
	def initParamString(name:String):Option[String] =
			Option(peer getInitParameter name)
	
	def initParamInt(name:String):Option[Int] =
			initParamString(name) flatMap { _.toIntOption }
		
	def initParamLong(name:String):Option[Long] =
			initParamString(name) flatMap { _.toLongOption }
}
