package scwebapp
package format

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

import scutil.lang._
import scutil.implicits._

object HeaderUnparser {
	// TODO make sure responses with a cookie are not cached
	
	// NOTE this always compiles to version 0 and does no quotiong at ass
	// forbidden in name and value: space, [ ] ( ) = , " / ? @ : ;
	def setCookieValue(
		name:String,
		value:String,
		domain:Option[String]		= None,
		path:Option[String]			= None,	
		comment:Option[String]		= None,
		maxAge:Option[HttpDuration]	= None,	// None deletes on browser exit, zero deletes immediately
		secure:Boolean				= false,
		httpOnly:Boolean			= false
		//version:Int				= 0		// 0=netscape, 1=RFC
	):String = {
		require(name.length != 0, "bad cookie name")
		
		// NOTE if we have a comment or anything is quoted, we need version 1
		// NOTE jetty seems to use "EEE, dd-MMM-yy HH:mm:ss zzz" for an expires header
		
		def alwaysValue(name:String, value:String):Option[ISeq[String]]	=
				Some(Vector(name, value))
		def simpleValue(name:String, value:Option[String]):Option[ISeq[String]]	=
				value	map { it => Vector(name,	it) }
		def simpleFlag(name:String, value:Boolean):Option[ISeq[String]]	=
				value	guard Vector(name)
			
		// NOTE these are not quoted
		val (maxAgeValue, expiresValue)	=
				maxAge
				.map { duration =>
					val expiresDate	=
							if (duration == HttpDuration.zero)	HttpDate.zero
							else								HttpDate.now + duration
					(HttpDuration unparse duration, HttpDate unparse expiresDate)
				}
				.unzip
			
		val values	=
				Vector(
					alwaysValue(name,		value),
					simpleValue("Path",		path),
					simpleValue("Domain",	domain),
					simpleValue("Expires",	expiresValue),
					simpleValue("Max-Age",	maxAgeValue),
					simpleFlag("Secure",	secure),
					simpleFlag("HttpOnly",	httpOnly),
					simpleValue("Comment",	comment)
				)
				
		values.collapse map { _ mkString "=" } mkString ";"
	}
}
