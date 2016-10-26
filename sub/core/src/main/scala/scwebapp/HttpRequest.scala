package scwebapp

import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._
import scutil.codec._

import scwebapp.factory.mimeType
import scwebapp.header._
import scwebapp.data._
import scwebapp.format._

trait HttpRequest {
	//------------------------------------------------------------------------------
	//## metadata
	
	def remoteUser:Option[String]
	def secure:Boolean
	
	def method:Tried[String,HttpMethod]

	// requestURI		always contains contextPath, servletPath and pathInfo but is still URL-encoded
	// ROOT context	contextPath is empty
	// mapping /foo/*	servletPath is "/foo", pathInfo contains the rest
	// mapping /*		servletPath is empty, pathInfo contains the rest
	// *.foo mapping	servletPath contains everything below the context, pathInfo is null
	
	/** full path including the contextPath */
	def uri:String
	
	/*
	// decoded according to server settings which by default (in tomcat) is ISO-8859-1.
	// this is not influenced by setCharacterEncoding or setEncoding
	def fullPathServlet:String	=
			ISeq(peer.getServletPath, peer.getPathInfo) filter { _ != null } mkString ""
		
	def pathInfoServlet:Option[String]	=
			Option(peer.getPathInfo)
	*/
	
	/** context of the web app */
	def contextPath:String
	
	def servletPath:String
	
	/** the full path after the context path, not yet url-decoded */
	final def fullPathRaw:String	=
			uri	cutPrefix contextPath getOrError so"expected uri ${uri} to start with context path ${contextPath}"

	final def pathInfoRaw:String	=
			fullPathRaw	cutPrefix servletPath getOrError so"expected uri ${uri} to start with context path ${contextPath} and servlet path ${servletPath}"

	final def fullPath(encoding:Charset):Tried[URIComponentProblem,String]	=
			URIComponent forCharset encoding decode fullPathRaw
		
	final def pathInfo(encoding:Charset):Tried[URIComponentProblem,String]	=
			URIComponent forCharset encoding decode pathInfoRaw
		
	final def fullPathUTF8:Tried[URIComponentProblem,String]	=
			fullPath(Charsets.utf_8)
		
	final def pathInfoUTF8:Tried[URIComponentProblem,String]	=
			pathInfo(Charsets.utf_8)
		
	def queryString:Option[String]
	
	final def queryParameters(encoding:Charset):Tried[String,CaseParameters]	=
			queryString cata (Win(CaseParameters.empty), UrlEncoding parseQueryParameters (_, encoding))
	
	final def queryParametersUTF8:Tried[String,CaseParameters]	=
			queryParameters(Charsets.utf_8)

	//------------------------------------------------------------------------------
	//## headers
	
	def headers:HttpHeaders
	
	//------------------------------------------------------------------------------
	//## content
	
	final def formParameters(defaultEncoding:Charset):Tried[String,CaseParameters]	=
			for {
				contentType	<- (headers first ContentType):Tried[String,Option[ContentType]]
				mime		<- contentType map { _.typ }						toWin	so"missing content type"
				_			<- mime sameMajorAndMinor mimeType.application_form	trueWin	so"unexpected content type ${mime.value}"
				encodingOpt	<- mime.charset
				string		= body readString Charsets.us_ascii
				encoding	= encodingOpt getOrElse defaultEncoding
				params		<- UrlEncoding parseForm (string, encoding)
			}
			yield params
			
	final def formParametersUTF8:Tried[String,CaseParameters]	=
			formParameters(Charsets.utf_8)
		
	def parameters:CaseParameters
	
	def body:HttpInput
	
	def parts:Tried[HttpPartsProblem,ISeq[HttpPart]]
}
