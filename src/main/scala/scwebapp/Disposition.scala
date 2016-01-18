package scwebapp

import scwebapp.format._

object Disposition {
	def unparse(it:Disposition):String	= {
		val typ				= Vector(DispositionType unparse it.typ)
		val fileName		= it.fileName map Quoting.quoteSimple
		val fileNameStar	= it.fileName map Quoting.quoteStar
		(typ ++ fileName ++ fileNameStar).flatten mkString ";"
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
