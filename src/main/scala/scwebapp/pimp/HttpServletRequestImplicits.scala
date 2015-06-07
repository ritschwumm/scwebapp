package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }
import java.io._
import java.nio.charset.Charset

import javax.servlet.ServletException
import javax.servlet.http._

import scutil.lang._
import scutil.implicits._
import scutil.io.Base64
import scutil.io.Charsets
import scutil.io.URIComponent
import scutil.io.Charsets.utf_8
import scutil.time.MilliInstant

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
			.getOrError (s"unexpected method ${peer.getMethod}")
	
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
	//## headers
	
	def headers:NoCaseParameters	=
			NoCaseParameters(
				for {	
					name	<- (EnumerationUtil toIterator peer.getHeaderNames.asInstanceOf[JEnumeration[String]]).toVector
					value	<- (EnumerationUtil toIterator (peer getHeaders name).asInstanceOf[JEnumeration[String]]).toVector
				}
				yield name -> value
			)
		
	//------------------------------------------------------------------------------
	//## special headers
	
	/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
	def contentLength:Tried[String,Option[Long]]	=
			(headers firstString "Content-Length")
			.map { it =>
				it guardBy { _ matches "\\d+" } flatMap { _.toLongOption } toWin s"invalid content length ${it}"
			}
			.sequenceTried
			
	/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
	def contentType:Tried[String,Option[MimeType]]	=
			(headers firstString "Content-Type")
			.map { it =>
				MimeType parse it toWin s"invalid content type ${it}"
			}
			.sequenceTried
			
	/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
	def encoding:Tried[String,Option[Charset]]	=
			contentType.toOption.flatten cata (
				Win(None),
				contentType => {
					(contentType.parameters firstString "charset")
					.map { it =>
						Charsets byName it mapFail constant(s"invalid charset ${it}")
					}
					.sequenceTried
				}
			)
			
	// TODO use the request's encoding here?
	/**
	Fail is invalid, Win(None) if missing, Win(Some) if valid
	@see http://www.ietf.org/rfc/rfc2617.txt
	*/
	def authorizationBasic(encoding:Charset):Tried[String,Option[(String,String)]]	=
			(headers firstString "Authorization")
			.map { header =>
				for {
					code	<- header cutPrefix "Basic "							toWin	s"missing Basic prefix in ${header}"
					bytes	<- Base64 decode code									toWin	s"invalid base64 code in ${code}"
					str		<- Catch.exception in (new String(bytes, encoding))	mapFail		constant("invalid string bytes")
					pair	<- new String(bytes, encoding) splitAroundFirstChar ':'	toWin	s"missing colon separator in ${str}"
				}
				yield pair
			}
			.sequenceTried
	
	def cookies:CaseParameters	=
			(headers firstString "Cookie")
			.flatMap	(HttpParser.parseCookie)
			.getOrElse	(CaseParameters.empty)
	
	/*
	def cookies:CaseParameters	=
			CaseParameters(
				for {
					cookie	<- peer.getCookies.guardNotNull.flattenMany
				}
				yield cookie.getName -> cookie.getValue
			)
	*/

	//------------------------------------------------------------------------------
	//## parameters
	
	def parameters:CaseParameters	=
			CaseParameters(
				for {	
					name	<- (EnumerationUtil toIterator peer.getParameterNames.asInstanceOf[JEnumeration[String]]).toVector
					value	<- peer getParameterValues name
				}
				yield name -> value
			)
			
	//------------------------------------------------------------------------------
	//## body
	
	// TODO ServletRequest has getReader which uses the supplied encoding!
	
	def body:HttpInput	=
			new HttpInput {
				def inputStream[T](handler:InputStream=>T):T	= handler(peer.getInputStream)
			}
	
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
	//## attributes
	
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			new HttpAttribute[T](
					()	=> (peer getAttribute name).asInstanceOf[T],
					t	=> peer setAttribute (name, t),
					()	=> peer removeAttribute name)
}
