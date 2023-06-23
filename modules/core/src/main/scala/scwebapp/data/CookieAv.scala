package scwebapp.data

import scwebapp.format.*
import scparse.ng.text.*

object CookieAv {
	lazy val parser:TextParser[CookieAv]	=
		parsers.cookie_av

	def unparse(it:CookieAv):String	=
		it match {
			case Expires(date)			=> "Expires="	+ (HttpDate		unparse date)
			case MaxAge(duration)		=> "Max-Age="	+ (HttpDuration	unparse duration)
			case Domain(domain)			=> "Domain="	+ domain
			case Path(path)				=> "Path="		+ path
			case Secure					=> "Secure"
			case HttpOnly				=> "HttpOnly"
			case Extension(extension)	=> extension
		}

	private object parsers {
		import HttpParsers.*

		val SEMICOLON	= TextParser is ';'

		lazy val cookie_av:TextParser[CookieAv]			=
			expires_av	orElse
			max_age_av	orElse
			domain_av	orElse
			path_av		orElse
			secure_av	orElse
			httponly_av	orElse
			extension_av
		lazy val expires_av:TextParser[CookieAv]		= TextParser isString "Expires="	right sane_cookie_date	map	Expires.apply
		lazy val max_age_av:TextParser[CookieAv]		= TextParser isString "Max-Age="	right duration			map MaxAge.apply
		lazy val domain_av:TextParser[CookieAv]			= TextParser isString "Domain="		right domain_value		map Domain.apply
		// TODO hack
		lazy val domain_value:TextParser[String]		= something
		lazy val path_av:TextParser[CookieAv]			= TextParser isString "Path="		right path_value		map Path.apply
		lazy val path_value:TextParser[String]			= something
		lazy val secure_av:TextParser[CookieAv]			= TextParser isString "Secure"		tag Secure
		lazy val httponly_av:TextParser[CookieAv]		= TextParser isString "HttpOnly"	tag HttpOnly
		lazy val extension_av:TextParser[CookieAv]		= something							map Extension.apply
		// TODO hack
		lazy val sane_cookie_date:TextParser[HttpDate]	= something mapFilter HttpDate.parse named "HttpDate"

		//// helper
		lazy val duration:TextParser[HttpDuration]		= longPositive map HttpDuration.apply
		lazy val something:TextParser[String]			= ((CTL orElse SEMICOLON).not right CHAR).seq.stringify
	}
}

enum CookieAv {
	case Expires(date:HttpDate)
	case MaxAge(duration:HttpDuration)
	case Domain(domain:String)
	case Path(path:String)
	case Secure
	case HttpOnly
	case Extension(extension:String)
}
