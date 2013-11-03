package scwebapp
package api

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
			peer.getInitParameterNames.asInstanceOf[JEnumeration[String]].asScala.toVector map { _ firstBy peer.getInitParameter }
			
	def initParamNames:Seq[String]	=
			peer.getInitParameterNames.asInstanceOf[JEnumeration[String]].asScala.toVector
			
	def initParamExists(name:String):Boolean	=
			(peer getInitParameter name) != null
			
	def initParamString(name:String):Option[String] =
			Option(peer getInitParameter name)
	
	def initParamInt(name:String):Option[Int] =
			initParamString(name) flatMap { _.toIntOption }
		
	def initParamLong(name:String):Option[Long] =
			initParamString(name) flatMap { _.toLongOption }
}
