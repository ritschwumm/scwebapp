package scwebapp.data

import scutil.base.implicits._
import scutil.lang._

import scwebapp.format._
import scparse.ng.text._

object BasicAuthenticate {
	lazy val parser:TextParser[BasicAuthenticate]	= parsers.value

	def unparse(it:BasicAuthenticate):String	=
		show"""Basic realm="${it.realm}""""

	private object parsers {
		import HttpParsers._

		final case class Challenge(name:String, parameters:Seq[(String,String)])

		val simpleParameter:TextParser[(String,String)]	= regParameter map { _._2 }
		val challenge:TextParser[Challenge]				= token next hash(simpleParameter) map Challenge.tupled
		val challengeList:TextParser[Nes[Challenge]]		= hash1(challenge)

		// TODO handle more challenge kinds
		// TODO handle charset parameter in Basic
		def findBasicRealm(it:Nes[Challenge]):Option[BasicAuthenticate]	=
			for {
				ch	<- it.toVector	collectFirst { case Challenge("Basic", params)	=> params	}
				rlm	<- ch			collectFirst { case ("realm", realm)			=> realm	}
			}
			yield BasicAuthenticate(rlm)

		val value:TextParser[BasicAuthenticate]	=
			challengeList collapseMap findBasicRealm named "basic realm"
	}
}

// TODO restrict allowed string
final case class BasicAuthenticate(realm:String)
