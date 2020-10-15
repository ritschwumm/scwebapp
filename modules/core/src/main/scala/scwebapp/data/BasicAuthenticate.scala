package scwebapp.data

import scutil.base.implicits._
import scutil.lang._

import scwebapp.format._
import scparse.ng.text._

object BasicAuthenticate {
	lazy val parser:TextParser[BasicAuthenticate]	= parsers.value

	def unparse(it:BasicAuthenticate):String	= {
		val parameters	= Vector("realm" -> it.realm) ++ it.charset.map("charset" -> _)
		"Basic " + parameters.map{case (name, value) => show"""$name="$value""""}.mkString(", ")
	}

	private object parsers {
		import HttpParsers._

		final case class Challenge(name:String, parameters:Seq[(String,String)])

		val simpleParameter:TextParser[(String,String)]	= regParameter map { _._2 }
		val challenge:TextParser[Challenge]				= token next hash(simpleParameter) map Challenge.tupled
		val challengeList:TextParser[Nes[Challenge]]	= hash1(challenge)

		// TODO handle more challenge kinds
		def findBasicRealm(it:Nes[Challenge]):Option[BasicAuthenticate]	=
			for {
				ch	<- it.toVector	collectFirst { case Challenge("Basic", params)	=> params	}
				rlm	<- ch			collectFirst { case ("realm", realm)			=> realm	}
			}
			yield {
				val charset	= ch collectFirst { case ("charset", charset)	=> charset	}
				BasicAuthenticate(rlm, charset)
			}

		val value:TextParser[BasicAuthenticate]	=
			challengeList collapseMap findBasicRealm named "basic realm"
	}
}

// TODO restrict allowed string
// TODO use a Charset here?
final case class BasicAuthenticate(realm:String, charset:Option[String])
