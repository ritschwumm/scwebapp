package scwebapp.servlet

import java.util.{ Enumeration=>JEnumeration }
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest

import scala.collection.JavaConverters._

import scutil.lang._
import scutil.implicits._
import scutil.io.Base64
import scutil.io.Charsets

import scwebapp._
import scwebapp.data._

private final class HttpRequestImpl(peer:HttpServletRequest) extends HttpRequest {
	//------------------------------------------------------------------------------
	//## metadata
	
	def remoteUser:Option[String]	=
			Option(peer.getRemoteUser)
	
	def method:Tried[String,HttpMethod]	=
			HttpMethod lookup peer.getMethod toWin peer.getMethod
	
	def secure:Boolean	= peer.isSecure

	/** full path including the contextPath */
	def uri:String	= peer.getRequestURI
	
	/** context of the web app */
	def contextPath:String	= peer.getContextPath
	
	def servletPath:String	= peer.getServletPath
	
	def queryString:Option[String]	= Option(peer.getQueryString)
			
	//------------------------------------------------------------------------------
	//## headers
	
	def headers:HttpHeaders	=
			HttpHeaders(
				NoCaseParameters(
					for {	
						name	<- peer.getHeaderNames.asInstanceOf[JEnumeration[String]].asScala.toVector
						value	<- (peer getHeaders name).asInstanceOf[JEnumeration[String]].asScala
					}
					yield name -> value
				)
			)
	
	//------------------------------------------------------------------------------
	//## content
	
	def parameters:CaseParameters	=
			CaseParameters(
				for {	
					name	<- peer.getParameterNames.asInstanceOf[JEnumeration[String]].asScala.toVector
					value	<- peer getParameterValues name
				}
				yield name -> value
			)
	
	def body:HttpInput	=
			new HttpInput {
				def withInputStream[T](handler:InputStream=>T):T	=
						peer.getInputStream use handler
			}
	
	def parts:Tried[HttpPartsProblem,ISeq[HttpPart]]	=
			catchHttpPartsProblem(peer.getParts.asScala.toVector) map { _ map { new HttpPartImpl(_) } }
			
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
