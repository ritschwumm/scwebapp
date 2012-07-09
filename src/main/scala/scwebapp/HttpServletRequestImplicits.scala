package scwebapp

import java.nio.charset.Charset
import javax.servlet.http._

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
	
	def paramExists(name:String):Boolean	=
			(delegate getParameter name) != null
			
	def paramString(name:String):Option[String] =
			Option(delegate getParameter name)
	
	def paramInt(name:String):Option[Int] =
			paramString(name) flatMap { _.toIntOption }
		
	def paramLong(name:String):Option[Long] =
			paramString(name) flatMap { _.toLongOption }
	
	//------------------------------------------------------------------------------
	
	def headerString(name:String):Option[String] = 
			Option(delegate getHeader name)
			
	def headerInt(name:String):Option[Int] = 
			headerString(name) flatMap { _.toIntOption }
			
	def headerLong(name:String):Option[Long] = 
			headerString(name) flatMap { _.toLongOption }
		
	//------------------------------------------------------------------------------
	
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			HttpAttribute request (delegate, name)
			
	//------------------------------------------------------------------------------
	
	def cookies:Seq[Cookie]	= 
			delegate.getCookies.guardNotNull.toSeq.flatten
	
	/*
	// NOTE this is an unfold
	
	val names	= delegate.getHeaderNames
	while (names.hasMoreElements) {
		val	name	= names.nextElement
		val values	= delegate getHeaders name.asInstanceOf[String]
		while (values.hasMoreElements) {
			val	value	= values.nextElement
			println(name + "=" + value)
		}
	}
	*/
}
