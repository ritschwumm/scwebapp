package scwebapp.data

import scwebapp.format._
import scwebapp.parser.string._

object CookieAv {
	lazy val parser:CParser[CookieAv]	=
			parsers.cookie_av
		
	def unparse(it:CookieAv):String	=
			it match {
				case ExpiresAv(date)		=> "Expires="	+ (HttpDate		unparse date)
				case MaxAgeAv(duration)		=> "Max-Age="	+ (HttpDuration	unparse duration)
				case DomainAv(domain)		=> "Domain="	+ domain
				case PathAv(path)			=> "Path="		+ path
				case SecureAv				=> "Secure"
				case HttpOnlyAv				=> "HttpOnly"
				case ExtensionAv(extension)	=> extension
			}
			
	private object parsers {
		import HttpParsers._
		
		lazy val cookie_av:CParser[CookieAv]			=
				expires_av	orElse
				max_age_av	orElse
				domain_av	orElse
				path_av		orElse
				secure_av	orElse
				httponly_av	orElse
				extension_av
		lazy val expires_av:CParser[CookieAv]			= sis("Expires=")	right sane_cookie_date	map	ExpiresAv.apply
		lazy val max_age_av:CParser[CookieAv]			= sis("Max-Age=")	right duration			map MaxAgeAv.apply
		lazy val domain_av:CParser[CookieAv]			= sis("Domain=")	right domain_value		map DomainAv.apply
		// TODO hack
		lazy val domain_value:CParser[String]			= something
		lazy val path_av:CParser[CookieAv]				= sis("Path=")		right path_value		map PathAv.apply
		lazy val path_value:CParser[String]				= something
		lazy val secure_av:CParser[CookieAv]			= sis("Secure")		tag SecureAv
		lazy val httponly_av:CParser[CookieAv]			= sis("HttpOnly")	tag HttpOnlyAv
		lazy val extension_av:CParser[CookieAv]			= something									map ExtensionAv.apply
		// TODO hack
		lazy val sane_cookie_date:CParser[HttpDate]		= something filterMap HttpDate.parse
		
		//// helper
		lazy val duration:CParser[HttpDuration]			= longPositive map HttpDuration.apply
		lazy val something:CParser[String]				= ((CTL orElse cis(';')).prevent right CHAR).seq.stringify
	}
}

sealed trait CookieAv
case class ExpiresAv(date:HttpDate)			extends CookieAv
case class MaxAgeAv(duration:HttpDuration)	extends CookieAv
case class DomainAv(domain:String)			extends CookieAv
case class PathAv(path:String)				extends CookieAv
case object SecureAv						extends CookieAv
case object HttpOnlyAv						extends CookieAv
case class ExtensionAv(extension:String)	extends CookieAv
