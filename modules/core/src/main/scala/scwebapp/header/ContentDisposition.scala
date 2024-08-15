package scwebapp.header

import scutil.core.implicits.*

import scwebapp.HeaderType
import scwebapp.data.*
import scwebapp.format.*
import scparse.ng.text.*

object ContentDisposition extends HeaderType[ContentDisposition] {
	val key	= "Content-Disposition"

	def parse(it:String):Option[ContentDisposition]	=
		parsers.finished.parseString(it).toOption

	def unparse(it:ContentDisposition):String	= {
		// TODO filter out bad characters in value
		val typ				= ContentDispositionType.unparse(it.typ)
		val fileName		= it.fileName.map{ it => show"filename=${HttpUnparsers.value(it)}"				}
		val fileNameStar	= it.fileName.map{ it => show"filename*=${HttpUnparsers.quoteStar_UTF8(it)}"	}
		val parts			= Vector(typ) ++ fileName.toVector ++ fileNameStar.toVector
		parts mkString ";"
	}

	private object parsers {
		import HttpParsers.*

		// TODO handle *filename
		val value:TextParser[ContentDisposition]	=
			ContentDispositionType.parser.next(parameterList).map { (kind, params)	=>
				val filename	= params.firstString("filename")
				ContentDisposition(kind, filename)
			}

		val finished:TextParser[ContentDisposition]	= value.finishRight(LWSP)
	}
}

final case class ContentDisposition(
	typ:ContentDispositionType,
	fileName:Option[String]
)
