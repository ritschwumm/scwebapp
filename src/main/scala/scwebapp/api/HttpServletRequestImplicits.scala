package scwebapp
package api

import java.util.{ Enumeration=>JEnumeration }
import java.nio.charset.Charset
import javax.servlet.http._

import scala.collection.JavaConverters._

import scutil.Implicits._
import scutil.Base64

object HttpServletRequestImplicits extends HttpServletRequestImplicits

trait HttpServletRequestImplicits {
	implicit def extendHttpServletRequest(delegate:HttpServletRequest):HttpServletRequestExtension	= 
			new HttpServletRequestExtension(delegate)
}

final class HttpServletRequestExtension(delegate:HttpServletRequest) {
	def fullPath:String	= Seq(delegate.getServletPath, delegate.getPathInfo) filter { _ != null } mkString ""
			
	def remoteUser:Option[String]	= Option(delegate.getRemoteUser)

	/** see http://www.ietf.org/rfc/rfc2617.txt */
	def authorizationBasic:Option[(String,String)]	=
			for {
				header	<- Option(delegate getHeader "Authorization")
				code	<- header cutPrefix "Basic "
				bytes	<- Base64 unapply code
				// TODO check UTF-8 makes sense here
				pair	<- new String(bytes, "UTF-8") splitAroundFirst ':'
			}
			yield pair
			
	def setEncoding(encoding:Charset) {
		delegate setCharacterEncoding encoding.name
	}
			
	//------------------------------------------------------------------------------
	
	def parameters:Seq[(String,String)]	=
			for {	
				name	<- delegate.getParameterNames.asInstanceOf[JEnumeration[String]].asScala.toSeq
				value	<- (delegate getParameterValues name)
			}
			yield (name, value)
			
	def paramExists(name:String):Boolean	=
			(delegate getParameter name) != null
			
	def paramString(name:String):Option[String] =
			Option(delegate getParameter name)
	
	def paramInt(name:String):Option[Int] =
			paramString(name) flatMap { _.toIntOption }
		
	def paramLong(name:String):Option[Long] =
			paramString(name) flatMap { _.toLongOption }
	
	//------------------------------------------------------------------------------
	
	def headers:Seq[(String,String)]	=
			for {	
				name	<- delegate.getHeaderNames.asInstanceOf[JEnumeration[String]].asScala.toSeq
				value	<- (delegate getHeaders name).asInstanceOf[JEnumeration[String]].asScala
			}
			yield (name, value)
			
	def headerString(name:String):Option[String] = 
			Option(delegate getHeader name)
			
	def headerInt(name:String):Option[Int] = 
			headerString(name) flatMap { _.toIntOption }
			
	def headerLong(name:String):Option[Long] = 
			headerString(name) flatMap { _.toLongOption }
		
	//------------------------------------------------------------------------------
	
	def cookies:Seq[(String,String)]	=
			delegate.getCookies.guardNotNull.toSeq.flatten map { it => (it.getName, it.getValue) }
		
	//------------------------------------------------------------------------------
	
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			new HttpAttribute[T](
					()	=> (delegate getAttribute name).asInstanceOf[T],
					t	=> delegate setAttribute (name, t),
					()	=> delegate removeAttribute name)
}
