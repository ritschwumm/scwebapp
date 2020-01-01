package scwebapp.data

import scwebapp.format._
import scparse.ng.text._

object MediaValue {
	lazy val parser:TextParser[MediaValue]	= parsers.value

	def unparse(it:MediaValue):String	=
			(MediaPattern	unparse			it.pattern)		+
			(HttpUnparsers	parameterList	it.parameters)	+
			(HttpUnparsers	qParamPart		it.quality)

	private object parsers {
		import HttpParsers._

		// NOTE this is quite a hack in the RFC: the q-value separates media type parameters from media range parameters...
		val manyParametersStopQ:TextParser[Seq[(Boolean,(String,String))]]	= (qParam.prevents right nextParameter).seq
		val parameterListStopQ:TextParser[NoCaseParameters]					= manyParametersStopQ map { list => NoCaseParameters(extendedFirst(list)) }

		val value:TextParser[MediaValue]	=
				MediaPattern.parser next parameterListStopQ next qParam.option map { case ((p, ps), q) => MediaValue(p, ps, q) }
	}
}

final case class MediaValue(pattern:MediaPattern, parameters:NoCaseParameters, quality:Option[QValue]) {
	// TODO include parameters in the match of the pattern?
	val totalQuality	= quality getOrElse QValue.one

	def acceptance(typ:MimeType):Option[(Int,QValue)]	=
			pattern matches typ map { _ -> totalQuality }
}
