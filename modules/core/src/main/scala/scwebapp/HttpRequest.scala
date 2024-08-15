package scwebapp

import java.nio.charset.Charset

import scutil.core.implicits.*
import scutil.lang.*

import scwebapp.factory.mimeType
import scwebapp.header.*
import scwebapp.data.*
import scwebapp.format.*

//	requestURI		always contains contextPath, servletPath and pathInfo but is still URL-encoded
//
//	ROOT context	contextPath is empty
//	mapping /foo/*	servletPath is "/foo", pathInfo contains the rest
//	mapping /*		servletPath is empty, pathInfo contains the rest
//	mapping *.foo	servletPath contains everything below the context, pathInfo is null

trait HttpRequest {
	//------------------------------------------------------------------------------
	//## metadata

	def remoteUser:Option[String]

	def remoteIp:String
	def remotePort:Int

	def localIp:String
	def localPort:Int

	def secure:Boolean

	def method:Either[String,HttpMethod]

	//-----------------------------------------------------------------------------
	//## path

	/** full path including the contextPath */
	def uri:String

	/** context of the web app: empty for the root context, starting with a slash in a subcontext */
	def contextPath:String

	def servletPath:String

	/** the full path after the context path, not yet url-decoded */
	final def fullPath:HttpPath	=
		uri.cutPrefix(contextPath).map(HttpPath.apply).getOrError(show"expected uri ${uri} to start with context path ${contextPath}")

	/*
	// decoded according to server settings which by default (in tomcat) is ISO-8859-1.
	// this is not influenced by setCharacterEncoding or setEncoding
	def fullPathServlet:String	=
		Seq(peer.getServletPath, peer.getPathInfo).filter(_ != null).mkString("")
	*/

	final def pathInfo:HttpPath	=
		fullPath.raw.cutPrefix(servletPath).map(HttpPath.apply).getOrError(show"expected uri ${uri} to start with context path ${contextPath} and servlet path ${servletPath}")

	/*
	def pathInfoServlet:Option[String]	=
		Option(peer.getPathInfo)
	*/

	//-----------------------------------------------------------------------------
	//## query parameters

	def queryString:Option[String]

	final def queryParameters(encoding:Charset):Either[String,CaseParameters]	=
		queryString.cata(Right(CaseParameters.empty), UrlEncoding.parseQueryParameters(_, encoding))

	final def queryParametersUTF8:Either[String,CaseParameters]	=
		queryParameters(Charsets.utf_8)

	//------------------------------------------------------------------------------
	//## headers

	def headers:HttpHeaders

	//------------------------------------------------------------------------------
	//## content

	def body:HttpInput

	final def formParameters(defaultEncoding:Charset):Either[String,CaseParameters]	=
		for {
			contentType	<- headers.first(ContentType):Either[String,Option[ContentType]]
			mime		<- contentType.map(_.typ)								.toRight(show"missing content type")
			_			<- mime.sameMajorAndMinor(mimeType.application_form)	.guardEither(show"unexpected content type ${mime.value}")
			encodingOpt	<- mime.charset
			string		= body.readString(Charsets.us_ascii)
			encoding	= encodingOpt.getOrElse(defaultEncoding)
			params		<- UrlEncoding.parseForm(string, encoding)
		}
		yield params

	final def formParametersUTF8:Either[String,CaseParameters]	=
		formParameters(Charsets.utf_8)

	def parts:Either[HttpPartsProblem,Seq[HttpPart]]
}
