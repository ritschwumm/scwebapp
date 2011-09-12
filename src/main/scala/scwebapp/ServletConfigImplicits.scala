package scwebapp

import java.util.{Enumeration=>JEnumeration}
import javax.servlet._

import scala.collection.JavaConverters._

import scutil.Implicits._

object ServletConfigImplicits extends ServletConfigImplicits

trait ServletConfigImplicits {
	implicit def extendServletConfig(delegate:ServletConfig):ServletConfigExtension		= 
			new ServletConfigExtension(delegate)
}

final class ServletConfigExtension(delegate:ServletConfig) {
	def initParamNames:Seq[String]	=
			delegate.getInitParameterNames.asInstanceOf[JEnumeration[String]].asScala.toSeq
			
	def initParamExists(name:String):Boolean	=
			(delegate getInitParameter name) != null
			
	def initParamString(name:String):Option[String] =
			Option(delegate getInitParameter name)
	
	def initParamInt(name:String):Option[Int] =
			initParamString(name) flatMap { _.toIntOption }
		
	def initParamLong(name:String):Option[Long] =
			initParamString(name) flatMap { _.toLongOption }
}
