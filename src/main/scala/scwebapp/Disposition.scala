package scwebapp

import scutil.implicits._

import scwebapp.format._

object Disposition {
	def unparse(it:Disposition):String	= {
		// TODO filter out bad characters in quoteSimple
		val typ				= DispositionType unparse it.typ
		val fileName		= it.fileName 	map { it => so"filename=${Quoting quoteSimple it}"	}
		val fileNameStar	= it.fileName	map { it => so"filename*=${Quoting quoteStar_UTF8 it}"	}
		val parts			= Vector(typ) ++ fileName.toVector ++ fileNameStar.toVector
		parts mkString ";"
	}
}

case class Disposition(
	typ:DispositionType,
	fileName:Option[String]
)
