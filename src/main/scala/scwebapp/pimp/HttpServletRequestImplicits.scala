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
import scutil.io.Charsets
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
			HttpMethods 
			.find		{ _.id == peer.getMethod.toUpperCase }
			.getOrError (s"unexpected method ${peer.getMethod}")
	
	def remoteUser:Option[String]	=
			Option(peer.getRemoteUser)

	//------------------------------------------------------------------------------
	//## paths
	
	// NOTE
	// requestURI		always contains contextPath, servletPath and pathInfo but is still URL-encoded
	// mapping /*		causes an empty servletPath
	// ROOT context		causes an empty contextPath
	// *.foo mapping	auses pathInfo to be null and a servletPath containing everything below the context
	
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
				fullPathRaw			cutPrefix
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
	
	def parameters:CaseParameters	=
			CaseParameters(
				for {	
					name	<- peer.getParameterNames.asInstanceOf[JEnumeration[String]].asScala.toVector
					value	<- peer getParameterValues name
				}
				yield name -> value
			)
	
	//------------------------------------------------------------------------------
	//## headers
	
	def headers:NoCaseParameters	=
			NoCaseParameters(
				for {	
					name	<- peer.getHeaderNames.asInstanceOf[JEnumeration[String]].asScala.toVector
					value	<- (peer getHeaders name).asInstanceOf[JEnumeration[String]].asScala
				}
				yield name -> value
			)
		
	//------------------------------------------------------------------------------
	//## special headers
	
	/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
	def encoding:Tried[String,Option[Charset]]	=
			peer.getCharacterEncoding.guardNotNull
			.map { name => Charsets byName name mapFail constant(name) }
			.sequenceTried
			
	/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
	def contentType:Tried[String,Option[MimeType]]	=
			peer.getContentType.guardNotNull
			.map { name => MimeType parse name toWin name }
			.sequenceTried
			
	// TODO add error when wrong?
	def contentLength:Option[Long]	=
			headers firstString "Content-Length" filter { _ matches "\\d+" } flatMap { _.toLongOption }
	
	/** see http://www.ietf.org/rfc/rfc2617.txt */
	def authorizationBasic(encoding:Charset):Option[(String,String)]	=
			for {
				header	<- Option(peer getHeader "Authorization")
				code	<- header cutPrefix "Basic "
				bytes	<- Base64 read code
				pair	<- new String(bytes, encoding) splitAroundFirst ':'
			}
			yield pair
	
	def cookies:CaseParameters	=
			CaseParameters(
				for {
					cookie	<- peer.getCookies.guardNotNull.flattenMany 
				}
				yield cookie.getName -> cookie.getValue
			)
		
	//------------------------------------------------------------------------------
	//## body
	
	// TODO ServletRequest has getReader which uses the supplied encoding!
	
	def body:HttpBody	=
			new HttpBody {
				def inputStream()	= peer.getInputStream
			}
	
	//------------------------------------------------------------------------------
	//## attributes
	
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			new HttpAttribute[T](
					()	=> (peer getAttribute name).asInstanceOf[T],
					t	=> peer setAttribute (name, t),
					()	=> peer removeAttribute name)
}
