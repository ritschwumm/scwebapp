package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }
import java.io._
import java.nio.charset.Charset

import javax.servlet.ServletException
import javax.servlet.http._

import scutil.lang._
import scutil.implicits._
import scutil.io._

import scwebapp.HttpInput
import scwebapp.factory.mimeType
import scwebapp.format._

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
			HttpMethod lookup peer.getMethod getOrError so"unexpected method ${peer.getMethod}"
	
	def remoteUser:Option[String]	=
			Option(peer.getRemoteUser)

	//------------------------------------------------------------------------------
	//## paths
	
	// requestURI		always contains contextPath, servletPath and pathInfo but is still URL-encoded
	// ROOT context	contextPath is empty
	// mapping /foo/*	servletPath is "/foo", pathInfo contains the rest
	// mapping /*		servletPath is empty, pathInfo contains the rest
	// *.foo mapping	servletPath contains everything below the context, pathInfo is null
	
	/*
	// decoded according to server settings which by default (in tomcat) is ISO-8859-1.
	// this is not influenced by setCharacterEncoding or setEncoding
	def fullPathServlet:String	=
			ISeq(peer.getServletPath, peer.getPathInfo) filter { _ != null } mkString ""
		
	def pathInfoServlet:Option[String]	=
			Option(peer.getPathInfo)
	*/
	
	def uri:String			= peer.getRequestURI
	def contextPath:String	= peer.getContextPath
	def servletPath:String	= peer.getServletPath
	
	/** the full path after the context path, not yet url-decoded */
	def fullPathRaw:String	=
			uri	cutPrefix contextPath getOrError so"expected uri ${uri} to start with context path ${contextPath}"
			
	def pathInfoRaw:String	=
			fullPathRaw	cutPrefix servletPath getOrError so"expected uri ${uri} to start with context path ${contextPath} and servlet path ${servletPath}"

	def fullPath(encoding:Charset):String	=
			URIComponent forCharset encoding decode fullPathRaw
		
	def pathInfo(encoding:Charset):String	=
			URIComponent forCharset encoding decode pathInfoRaw
		
	def fullPathUTF8:String	=
			fullPath(Charsets.utf_8)
		
	def pathInfoUTF8:String	=
			pathInfo(Charsets.utf_8)
		
	//------------------------------------------------------------------------------
	//## query
	
	def queryString:Option[String]	=
			Option(peer.getQueryString)
		
	def queryParameters(encoding:Charset):CaseParameters	=
			queryString cata (CaseParameters.empty, UrlEncoding parseQueryParameters (_, encoding))
	
	def queryParametersUTF8:CaseParameters	=
			queryParameters(Charsets.utf_8)
	
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
			HeaderParser contentLength headers
			
	def contentType:Tried[String,Option[MimeType]]	=
			HeaderParser contentType headers
			
	def encoding:Tried[String,Option[Charset]]	=
			HeaderParser encoding headers
			
	def authorizationBasic(encoding:Charset):Tried[String,Option[BasicAuthentication]]	=
			HeaderParser authorizationBasic (headers, encoding)
			
	def cookies:Tried[String,Option[CaseParameters]]	=
			HeaderParser cookies headers
	
	//------------------------------------------------------------------------------
	//## parameters
	
	def formParameters(defaultEncoding:Charset):Tried[String,CaseParameters]	=
			for {
				mimeOpt		<- contentType
				mime		<- mimeOpt											toWin	so"missing content type"
				_			<- mime sameMajorAndMinor mimeType.application_form	trueWin	so"unexpected content type ${mime.value}"
				encodingOpt	<- HeaderParser parseEncoding mime
			}
			yield {
				val string		= body readString Charsets.us_ascii
				val encoding	= encodingOpt getOrElse defaultEncoding
				UrlEncoding parseForm (string, encoding)
			}
			
	def formParametersUTF8:Tried[String,CaseParameters]	=
			formParameters(Charsets.utf_8)
	
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
			
	//------------------------------------------------------------------------------
	//## servlet context
	
	// NOTE without the toLowerCase hack this returns application/octet-stream for files with uppercase name extensions
	def mimeTypeFor(path:String):Option[MimeType]	=
			Option(peer.getServletContext getMimeType path.toLowerCase) flatMap MimeType.parse
}
