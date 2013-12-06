package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset

import javax.servlet.ServletException
import javax.servlet.http._

import scala.collection.JavaConverters._

import scutil.lang._
import scutil.Implicits._
import scutil.io.Base64
import scutil.io.URIComponent
import scutil.time.MilliInstant

object HttpServletRequestImplicits extends HttpServletRequestImplicits

trait HttpServletRequestImplicits {
	implicit def extendHttpServletRequest(peer:HttpServletRequest):HttpServletRequestExtension	= 
			new HttpServletRequestExtension(peer)
}

final class HttpServletRequestExtension(peer:HttpServletRequest) {
	/** note this does not influence how PathInfo and therefore FullPath are treated */
	def setEncoding(encoding:Charset) {
		peer setCharacterEncoding encoding.name
	}
	
	def method:HttpMethod	=
			HttpMethods find { _.id == peer.getMethod.toUpperCase } getOrError s"unexpected method ${peer.getMethod}"
	
	def remoteUser:Option[String]	=
			Option(peer.getRemoteUser)

	/** see http://www.ietf.org/rfc/rfc2617.txt */
	def authorizationBasic(encoding:Charset):Option[(String,String)]	=
			for {
				header	<- Option(peer getHeader "Authorization")
				code	<- header cutPrefix "Basic "
				bytes	<- Base64 read code
				pair	<- new String(bytes, encoding) splitAroundFirst ':'
			}
			yield pair
	
	//------------------------------------------------------------------------------
	//## paths
	
	// NOTE
	// requestURI	always contains contextPath, servletPath and pathInfo but is still URL-encoded
	// mapping /*	causes an empty servletPath
	// ROOT context causes an empty contextPath
	// *.foo mapping causes pathInfo to be null and a servletPath containing everything below the context
	
	/** 
	the full path after the context path, 
	decoded according to server settings which by default (in tomcat) is ISO-8859-1.
	this is not influenced by setCharacterEncoding or setEncoding 
	*/
	def fullPathServlet:String	=
			Seq(peer.getServletPath, peer.getPathInfo) filter { _ != null } mkString ""
	
	/** the full path after the context path, not yet url-decoded */
	def fullPathRaw:String	= 
			peer.getRequestURI	cutPrefix 
			peer.getContextPath	getOrError 
			s"expected RequestURI ${peer.getRequestURI} to start with context path ${peer.getContextPath}"
			
	/** the full path after the context path, URL-decoded with the given Charset */
	def fullPathUTF8:String	= 
			URIComponent decode fullPathRaw
		
	def pathInfoServlet:Option[String]	=
			Option(peer.getPathInfo)
	
	def pathInfoRaw:Option[String]	=
			pathInfoServlet.isDefined guard {
				fullPathRaw	cutPrefix
				peer.getServletPath	getOrError 
				s"expected RequestURI ${peer.getRequestURI} to start with context path ${peer.getContextPath} and servlet path ${peer.getServletPath}"
			}
			
	def pathInfoUTF8:Option[String]	=
			pathInfoRaw map URIComponent.decode

	//------------------------------------------------------------------------------
	//## multipart
	
	def part(name:String):Tried[HttpPartsProblem,Option[Part]]	=
			try {
				Win(Option(peer getPart name))
			}
			catch {
				case e:ServletException			=> Fail(NotMultipart(e))
				case e:IOException				=> Fail(InputOutputFailed(e))
				case e:IllegalStateException	=> Fail(SizeLimitExceeded(e))
			}
    
	def parts:Tried[HttpPartsProblem,Seq[Part]]	=
			try {
				Win(peer.getParts.asScala.toVector)
			}
			catch {
				case e:ServletException			=> Fail(NotMultipart(e))
				case e:IOException				=> Fail(InputOutputFailed(e))
				case e:IllegalStateException	=> Fail(SizeLimitExceeded(e))
			}
			
	//------------------------------------------------------------------------------
	//## parameters
	
	def parameters:Seq[(String,String)]	=
			for {	
				name	<- parameterNames
				value	<- peer getParameterValues name
			}
			yield (name, value)
			
	def parameterNames:Seq[String]	=
			peer.getParameterNames.asInstanceOf[JEnumeration[String]].asScala.toVector
			
	def paramString(name:String):Option[String] =
			Option(peer getParameter name)
	
	def paramInt(name:String):Option[Int] =
			paramString(name) flatMap { _.toIntOption }
		
	def paramLong(name:String):Option[Long] =
			paramString(name) flatMap { _.toLongOption }
	
	//------------------------------------------------------------------------------
	//## headers
	
	def headers:Seq[(String,String)]	=
			for {	
				name	<- headerNames
				value	<- (peer getHeaders name).asInstanceOf[JEnumeration[String]].asScala
			}
			yield (name, value)
			
	def headerNames:Seq[String]	=
			peer.getHeaderNames.asInstanceOf[JEnumeration[String]].asScala.toVector
		
	def headerString(name:String):Option[String] = 
			Option(peer getHeader name)
			
	def headerInt(name:String):Option[Int] = 
			headerString(name) flatMap { _.toIntOption }
			
	def headerLong(name:String):Option[Long] = 
			headerString(name) flatMap { _.toLongOption }
		
	def headerHttpDate(name:String):Option[HttpDate]	=
			headerString(name) flatMap { HttpDateFormat.parse }
		
	//------------------------------------------------------------------------------
	//## cookies
	
	def cookies:Seq[(String,String)]	=
			peer.getCookies.guardNotNull.flattenMany map { it => (it.getName, it.getValue) }
		
	//------------------------------------------------------------------------------
	//## attributes
	
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			new HttpAttribute[T](
					()	=> (peer getAttribute name).asInstanceOf[T],
					t	=> peer setAttribute (name, t),
					()	=> peer removeAttribute name)
}
