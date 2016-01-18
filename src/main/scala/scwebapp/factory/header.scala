package scwebapp
package factory

import scutil.lang._
import scutil.implicits._

import scwebapp.format._

object header extends header

trait header {
	private type Header	= (String,String)
	private def Header(name:String, value:String):Header	= name -> value
	
	def ETag(it:String):Header								= Header("ETag",					it)
	def Expires(it:HttpDate):Header							= Header("Expires",					HttpDate unparse it)
	def LastModified(it:HttpDate):Header					= Header("Last-Modified",			HttpDate unparse it)
	
	def ContentEncoding(it:ContentEncodingValue):Header		= Header("Content-Encoding",		ContentEncodingValue unparse it)
	def ContentDisposition(it:Disposition):Header			= Header("Content-Disposition",		Disposition unparse it)
	def ContentType(it:MimeType)							= Header("Content-Type",			MimeType unparse it)
	def ContentLength(it:Long)								= Header("Content-Length",			it.toString)
	def ContentRange(it:ResponseRange)						= Header("Content-Range",			ResponseRange unparse it)
	
	def AcceptRanges(it:RangeType):Header					= Header("Accept-Ranges",			RangeType unparse it)
	
	// "nosniff"
	def XContentTypeOptions(it:String):Header				= Header("X-Content-Type-Options",	it)
	// "IE=edge"
	def XUACompatible(it:String):Header						= Header("X-UA-Compatible",			it)
	
	// TODO proper types
	def Pragma(it:String):Header							= Header("Pragma",					it)
	def CacheControl(directives:String*):Header				= Header("Cache-Control",			directives mkString ", ")
	def WWWAuthenticate(realm:String):Header				= Header("WWW-Authenticate",		so"""Basic realm="${realm}"""")
		
	// forbidden in name and value: [ ] ( ) = , " / ? @ : ;
	def SetCookie(
		name:String,
		value:String,
		domain:Option[String]		= None,
		path:Option[String]			= None,	
		comment:Option[String]		= None,
		maxAge:Option[HttpDuration]	= None,	// None deletes on browser exit, zero deletes immediately
		secure:Boolean				= false,
		httpOnly:Boolean			= false
		// version:Int				= 0		// 0=netscape, 1=RFC
	):Header	= Header(
		"Set-Cookie",
		HeaderUnparser setCookieValue (
			name, value, domain, path, comment, maxAge, secure, httpOnly
		)
	)
	
	def NoCache:ISeq[Header]	=
			Vector(
				CacheControl("no-cache", "no-store", "must-revalidate"),
				Pragma("no-cache"),
				Expires(HttpDate.zero)
			)
}
