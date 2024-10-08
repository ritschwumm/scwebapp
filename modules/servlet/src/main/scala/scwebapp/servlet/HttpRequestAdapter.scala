package scwebapp.servlet

import java.util.{ Enumeration as JEnumeration }
import java.io.IOException
import java.io.InputStream

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest

import scala.jdk.CollectionConverters.*

import scutil.core.implicits.*

import scwebapp.*
import scwebapp.data.*

private final class HttpRequestAdapter(peer:HttpServletRequest) extends HttpRequest {
	//------------------------------------------------------------------------------
	//## metadata

	def remoteUser:Option[String]	=
		Option(peer.getRemoteUser)

	def remoteIp:String	= peer.getRemoteAddr
	def remotePort:Int	= peer.getRemotePort

	def localIp:String	= peer.getLocalAddr
	def localPort:Int	= peer.getLocalPort

	def method:Either[String,HttpMethod]	=
		HttpMethod.lookup(peer.getMethod).toRight(peer.getMethod)

	def secure:Boolean	= peer.isSecure

	//-----------------------------------------------------------------------------
	//## path

	def uri:String	= peer.getRequestURI

	def contextPath:String	= peer.getContextPath

	def servletPath:String	= peer.getServletPath

	//-----------------------------------------------------------------------------
	//## query

	def queryString:Option[String]	= Option(peer.getQueryString)

	//------------------------------------------------------------------------------
	//## headers

	@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
	def headers:HttpHeaders	=
		HttpHeaders(
			NoCaseParameters(
				for {
					name	<- peer.getHeaderNames.asInstanceOf[JEnumeration[String]].asScala.toVector
					value	<- peer.getHeaders(name).asInstanceOf[JEnumeration[String]].asScala
				}
				yield name -> value
			)
		)

	//------------------------------------------------------------------------------
	//## content

	def body:HttpInput	=
		new HttpInput {
			def withInputStream[T](handler:InputStream=>T):T	=
				peer.getInputStream.use(handler)
		}

	/*
	@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
	def parameters:CaseParameters	=
		CaseParameters(
			for {
				name	<- peer.getParameterNames.asInstanceOf[JEnumeration[String]].asScala.toVector
				value	<- peer.getParameterValues(name)
			}
			yield name -> value
		)
	*/

	def parts:Either[HttpPartsProblem,Seq[HttpPart]]	=
		catchHttpPartsProblem(peer.getParts.asScala.toVector).map(_.map(new HttpPartImpl(_)))

	private def catchHttpPartsProblem[T](it: =>T):Either[HttpPartsProblem,T]	=
		try {
			Right(it)
		}
		catch {
			case e:ServletException			=> Left(HttpPartsProblem.NotMultipart(e))
			case e:IOException				=> Left(HttpPartsProblem.InputOutputFailed(e))
			case e:IllegalStateException	=> Left(HttpPartsProblem.SizeLimitExceeded(e))
		}
}
