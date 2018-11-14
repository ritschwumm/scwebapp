package scwebapp.data

import scutil.lang._

import scwebapp.format._
import scwebapp.parser.string._

object MediaValue {
	lazy val parser:CParser[MediaValue]	= parsers.value

	def unparse(it:MediaValue):String	=
			(MediaPattern	unparse			it.pattern)		+
			(HttpUnparsers	parameterList	it.parameters)	+
			(HttpUnparsers	qParamPart		it.quality)

	private object parsers {
		import HttpParsers._

		// NOTE this is quite a hack in the RFC: the q-value separates media type parameters from media range parameters...
		val manyParametersStopQ:CParser[ISeq[(Boolean,(String,String))]]	= (qParam.prevent right nextParameter).seq
		val parameterListStopQ:CParser[NoCaseParameters]					= manyParametersStopQ map { list => NoCaseParameters(extendedFirst(list)) }

		val value:CParser[MediaValue]	=
				MediaPattern.parser next parameterListStopQ next qParam.option map { case ((p, ps), q) => MediaValue(p, ps, q) }
	}
}

final case class MediaValue(pattern:MediaPattern, parameters:NoCaseParameters, quality:Option[QValue]) {
	// TODO include parameters in the match of the pattern?
	val totalQuality	= quality getOrElse QValue.one

	def acceptance(typ:MimeType):Option[(Int,QValue)]	=
			pattern matches typ map { _ -> totalQuality }
}
