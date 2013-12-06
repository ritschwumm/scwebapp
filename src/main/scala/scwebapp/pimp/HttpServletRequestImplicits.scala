package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }
import java.io._
import java.nio.charset.Charset

import javax.servlet.ServletException
import javax.servlet.http._

import scala.collection.JavaConverters._

import scutil.lang._
import scutil.implicits._
import scutil.io.Base64
import scutil.io.URIComponent
import scutil.io.Charsets.utf_8
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
			catchHttpPartsProblem(Option(peer getPart name))
    
	def parts:Tried[HttpPartsProblem,Seq[Part]]	=
			catchHttpPartsProblem(peer.getParts.asScala.toVector)
			
	private def catchHttpPartsProblem[T](it: =>T):Tried[HttpPartsProblem,T]	=
			try {
				Win(it)
			}
			catch {
				case e:ServletException			=> Fail(NotMultipart(e))
				case e:IOException				=> Fail(InputOutputFailed(e))
				case e:IllegalStateException	=> Fail(SizeLimitExceeded(e))
			}
			
	//------------------------------------------------------------------------------
	//## parameters
	
	def parameters:Parameters	=
			new Parameters {
				def caseSensitive:Boolean	= true
				
				def all:Seq[(String,String)]	=
						for {	
							name	<- names
							value	<- peer getParameterValues name
						}
						yield (name, value)
						
				def names:Seq[String]	=
						peer.getParameterNames.asInstanceOf[JEnumeration[String]].asScala.toVector
					
				def firstString(name:String):Option[String] =
						Option(peer getParameter name)
			}
	
	//------------------------------------------------------------------------------
	//## headers
	
	def headers:Parameters	=
			new Parameters {
				def caseSensitive:Boolean	= false
				
				def all:Seq[(String,String)]	=
						for {	
							name	<- names
							value	<- (peer getHeaders name).asInstanceOf[JEnumeration[String]].asScala
						}
						yield (name, value)
						
				def names:Seq[String]	=
						peer.getHeaderNames.asInstanceOf[JEnumeration[String]].asScala.toVector
					
				def firstString(name:String):Option[String] = 
						Option(peer getHeader name)
			}
		
	//------------------------------------------------------------------------------
	//## cookies
	
	def cookies:Parameters	=
			new Parameters {
				def caseSensitive:Boolean	= true
				
				def all:Seq[(String,String)]	=
						peer.getCookies.guardNotNull.flattenMany map { it => (it.getName, it.getValue) }
						
				def names:Seq[String]	=
						all map { _._1 }
					
				def firstString(name:String):Option[String] = 
						all collectFirst { case (`name`, value) => value }
			}
			
		
	//------------------------------------------------------------------------------
	//## attributes
	
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			new HttpAttribute[T](
					()	=> (peer getAttribute name).asInstanceOf[T],
					t	=> peer setAttribute (name, t),
					()	=> peer removeAttribute name)
					
	//------------------------------------------------------------------------------
	//## stream
	
	// TODO handle exceptions
	
	def asString(encoding:Charset):String	=
			withReader(encoding) { _.readFully }
		
	def asStringUTF8:String	=
			asString(utf_8)
		
	def withReader[T](encoding:Charset)(func:Reader=>T):T	=
			new InputStreamReader(openInputStream(), encoding) use func
			
	def withInputStream[T](func:InputStream=>T):T	=
			openInputStream() use func
			
	def openInputStream():InputStream	= peer.getInputStream
}
