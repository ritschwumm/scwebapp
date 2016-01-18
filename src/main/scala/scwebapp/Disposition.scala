package scwebapp

import scutil.implicits._

import scwebapp.format._

object Disposition {
	def unparse(it:Disposition):String	= {
		// TODO use parameters for this?
		val typ				= DispositionType unparse it.typ
		val fileName		= it.fileName 		map { it => so"filename=${Quoting quoteSimple it}"	}
		val fileNameStar	= it.fileNameStar	map { it => so"filename*=${Quoting quoteStar it}"	}
		val parts			= Vector(typ) ++ fileName.toVector ++ fileNameStar.toVector
		parts mkString ";"
	}
}

case class Disposition(
	typ:DispositionType,
	fileName:Option[String],
	fileNameStar:Option[String]
) {
	def preferredFileName:Option[String]	=
			fileNameStar orElse fileName
}
