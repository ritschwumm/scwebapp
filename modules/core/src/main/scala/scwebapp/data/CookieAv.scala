package scwebapp.data

import scwebapp.format._
import scparse.ng.text._

object CookieAv {
	lazy val parser:TextParser[CookieAv]	=
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

		lazy val cookie_av:TextParser[CookieAv]			=
			expires_av	orElse
			max_age_av	orElse
			domain_av	orElse
			path_av		orElse
			secure_av	orElse
			httponly_av	orElse
			extension_av
		lazy val expires_av:TextParser[CookieAv]		= TextParser isString "Expires="	right sane_cookie_date	map	ExpiresAv.apply
		lazy val max_age_av:TextParser[CookieAv]		= TextParser isString "Max-Age="	right duration			map MaxAgeAv.apply
		lazy val domain_av:TextParser[CookieAv]			= TextParser isString "Domain="		right domain_value		map DomainAv.apply
		// TODO hack
		lazy val domain_value:TextParser[String]		= something
		lazy val path_av:TextParser[CookieAv]			= TextParser isString "Path="		right path_value		map PathAv.apply
		lazy val path_value:TextParser[String]			= something
		lazy val secure_av:TextParser[CookieAv]			= TextParser isString "Secure"		tag SecureAv
		lazy val httponly_av:TextParser[CookieAv]		= TextParser isString "HttpOnly"	tag HttpOnlyAv
		lazy val extension_av:TextParser[CookieAv]		= something							map ExtensionAv.apply
		// TODO hack
		lazy val sane_cookie_date:TextParser[HttpDate]	= something require HttpDate.parse named "HttpDate"

		//// helper
		lazy val duration:TextParser[HttpDuration]		= longPositive map HttpDuration.apply
		lazy val something:TextParser[String]			= ((CTL orElse TextParser.isChar(';')).prevents right CHAR).seq.stringify
	}

	//------------------------------------------------------------------------------

	final	case class ExpiresAv(date:HttpDate)			extends CookieAv
	final	case class MaxAgeAv(duration:HttpDuration)	extends CookieAv
	final	case class DomainAv(domain:String)			extends CookieAv
	final	case class PathAv(path:String)				extends CookieAv
			case object SecureAv						extends CookieAv
			case object HttpOnlyAv						extends CookieAv
	final	case class ExtensionAv(extension:String)	extends CookieAv
}

sealed trait CookieAv
