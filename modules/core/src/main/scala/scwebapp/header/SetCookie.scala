package scwebapp.header

import scutil.base.implicits._
import scutil.lang._

import scwebapp.HeaderType
import scwebapp.data._
import scwebapp.format._
import scwebapp.parser.string._

// TODO make sure responses with a cookie are not cached

// NOTE without expires and maxAge, browser deletes cookie on exit.
// NOTE to delete a cookie, set an expires in the past

object SetCookie extends HeaderType[SetCookie] {
	val key	= "Set-Cookie"

	def parse(it:String):Option[SetCookie]	=
			parsers.finished parseStringOption it

	def unparse(it:SetCookie):String	= {
		// NOTE if we have a comment or anything is quoted, we need version 1
		// NOTE jetty seems to use "EEE, dd-MMM-yy HH:mm:ss zzz" for an expires header

		/*
		// NOTE these are not quoted
		val (maxAgeValue, expiresValue)	=
				it.maxAge
				.map { duration =>
					val expiresDate	=
							if (duration == HttpDuration.zero)	HttpDate.zero
							else								HttpDate.now() + duration
					(HttpDuration unparse duration, HttpDate unparse expiresDate)
				}
				.unzip
		*/

		val avs:Vector[Option[CookieAv]]	=
				Vector[Option[CookieAv]](
					it.domain	map		CookieAv.DomainAv.apply,
					it.path		map		CookieAv.PathAv.apply,
					it.maxAge	map		CookieAv.MaxAgeAv.apply,
					it.expires	map		CookieAv.ExpiresAv.apply,
					it.secure	option	CookieAv.SecureAv,
					it.httpOnly	option	CookieAv.HttpOnlyAv
				)

		val headPart	= it.name + "=" + it.value
		val tailParts	= avs.collapse map CookieAv.unparse
		headPart +: tailParts mkString ";"
	}

	private object parsers {
		import HttpParsers._
		import CookieParsers._

		// TODO wrong
		lazy val finished:CParser[SetCookie]	= value inside OWS

		lazy val value:CParser[SetCookie]	=
				set_cookie_string map { case ((k, v), avs) =>
					SetCookie(
						name		= k,
						value		= v,
						domain		= avs collectFirst { case CookieAv.DomainAv(x)	=> x},
						path		= avs collectFirst { case CookieAv.PathAv(x)		=> x},
						maxAge		= avs collectFirst { case CookieAv.MaxAgeAv(x)	=> x},
						secure		= avs contains CookieAv.SecureAv,
						httpOnly	= avs contains CookieAv.HttpOnlyAv
					)
				}

		/*
		lazy val cookie_header:CParser[ISeq[(String,String)]]			= sis("Cookie:") right (cookie_string inside OWS)
		lazy val cookie_string:CParser[ISeq[(String,String)]]			= cookie_pair sepSeq cis(';')

		lazy val set_cookie_header:CParser[((String,String),ISeq[CookieAv])]	= sis("Set-Cookie:") right SP right set_cookie_string
		*/

		lazy val set_cookie_string:CParser[((String,String),ISeq[CookieAv])]	= cookie_pair next (cis(';') right CookieAv.parser).seq
	}
}

// forbidden in name and value: [ ] ( ) = , " / ? @ : ;
final case class SetCookie(
	name:String,
	value:String,
	domain:Option[String]		= None,
	path:Option[String]			= None,
	maxAge:Option[HttpDuration]	= None,	// None deletes on browser exit, zero deletes immediately
	expires:Option[HttpDate]	= None,
	secure:Boolean				= false,
	httpOnly:Boolean			= false
	// version:Int				= 0		// 0=netscape, 1=RFC
) {
	require(name.length != 0, "bad cookie name")
}
