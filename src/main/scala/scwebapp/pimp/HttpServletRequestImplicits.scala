package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }
import java.io._
import java.nio.charset.Charset

import javax.servlet.ServletException
import javax.servlet.http._

import scutil.lang._
import scutil.implicits._
import scutil.io.URIComponent

import scwebapp.HttpInput

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
			.getOrError (so"unexpected method ${peer.getMethod}")
	
	def remoteUser:Option[String]	=
			Option(peer.getRemoteUser)

	//------------------------------------------------------------------------------
	//## paths
	
	// NOTE
	// requestURI		always contains contextPath, servletPath and pathInfo but is still URL-encoded
	// mapping /*		causes an empty servletPath
	// ROOT context		causes an empty contextPath
	// *.foo mapping	causes pathInfo to be null and a servletPath containing everything below the context
	
	/**
	the full path after the context path,
	decoded according to server settings which by default (in tomcat) is ISO-8859-1.
	this is not influenced by setCharacterEncoding or setEncoding
	*/
	def fullPathServlet:String	=
			ISeq(peer.getServletPath, peer.getPathInfo) filter { _ != null } mkString ""
	
	/** the full path after the context path, not yet url-decoded */
	def fullPathRaw:String	=
			peer.getRequestURI	cutPrefix
			peer.getContextPath	getOrError
			so"expected RequestURI ${peer.getRequestURI} to start with context path ${peer.getContextPath}"
			
	/** the full path after the context path, URL-decoded with the given Charset */
	def fullPathUTF8:String	=
			URIComponent decode fullPathRaw
		
	def pathInfoServlet:Option[String]	=
			Option(peer.getPathInfo)
	
	def pathInfoRaw:Option[String]	=
			pathInfoServlet.isDefined guard {
				fullPathRaw			cutPrefix
				peer.getServletPath	getOrError
				so"expected RequestURI ${peer.getRequestURI} to start with context path ${peer.getContextPath} and servlet path ${peer.getServletPath}"
			}
			
	def pathInfoUTF8:Option[String]	=
			pathInfoRaw map URIComponent.decode
	
	//------------------------------------------------------------------------------
	//## headers
	
	def headers:NoCaseParameters	=
			NoCaseParameters(
				for {	
					name	<- peer.getHeaderNames.asInstanceOf[JEnumeration[String]].toIterator.toVector
					value	<- (peer getHeaders name).asInstanceOf[JEnumeration[String]].toIterator.toVector
				}
				yield name -> value
			)
		
	def contentLength:Tried[String,Option[Long]]	=
			HeaderParsers contentLength headers
			
	def contentType:Tried[String,Option[MimeType]]	=
			HeaderParsers contentType headers
			
	def encoding:Tried[String,Option[Charset]]	=
			HeaderParsers encoding headers
			
	def authorizationBasic(encoding:Charset):Tried[String,Option[(String,String)]]	=
			HeaderParsers authorizationBasic (headers, encoding)
			
	def cookies:CaseParameters	=
			HeaderParsers cookies headers
	
	//------------------------------------------------------------------------------
	//## parameters
	
	def parameters:CaseParameters	=
			CaseParameters(
				for {	
					name	<- peer.getParameterNames.asInstanceOf[JEnumeration[String]].toIterator.toVector
					value	<- peer getParameterValues name
				}
				yield name -> value
			)
			
	//------------------------------------------------------------------------------
	//## body
	
	def body:HttpInput	= HttpInput outofInputStream (thunk { peer.getInputStream })
	
	def part(name:String):Tried[HttpPartsProblem,Option[Part]]	=
			catchHttpPartsProblem(Option(peer getPart name))

	def parts:Tried[HttpPartsProblem,ISeq[Part]]	=
			catchHttpPartsProblem(peer.getParts.toIterable.toVector)
			
	private def catchHttpPartsProblem[T](it: =>T):Tried[HttpPartsProblem,T]	=
			try {
				Win(it)
			}
			catch {
				case e:ServletException			=> Fail(NotMultipart(e))
				case e:IOException				=> Fail(InputOutputFailed(e))
				case e:IllegalStateException	=> Fail(SizeLimitExceeded(e))
			}
}
