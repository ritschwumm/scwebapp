package scwebapp.format

import scutil.core.implicits._

import scwebapp.data._

object HttpUnparsers {
	def qParam(it:QValue):String	=
		"q=" + (QValue unparse it)

	def qParamPart(it:Option[QValue]):String	=
		it.cata(
			"",
			x => ";" + qParam(x)
		)

	// TODO ugly, rename
	def parameterList(it:Parameters):String	=
		if (it.all.nonEmpty)	";" + parameters(it)
		else					""

	def parameters(it:Parameters):String	=
		it.all
		.map { case (k, v)	=> k + "=" + value(v) }
		.mkString	(";")

	//------------------------------------------------------------------------------

	def value(s:String):String	=
		s.exists(HttpParsers.nonTokenChars.toSet).cata(
			s,
			quotedString(s)
		)

	def quotedString(s:String):String	=
		"\"" +
		(
			s flatMap {
				case '"'	=> "\\\""
				case '\\'	=> "\\\\"
				case '\r'	=> "\\\r"
				case '\n'	=> "\\\n"
				case x		=> x.toString
			}
		).toString +
		"\""

	def quoteStar_UTF8(s:String):String	=
		"UTF-8''" + (s getBytes "UTF-8" map quoteStar1 mkString "")

	def quoteStar_ISO_8859_1(s:String):String	=
		"ISO-8859-1''" + (s getBytes "ISO-8859-1" map quoteStar1 mkString "")

	private def quoteStar1(c:Byte):String	=
		c match {
			case x
			if	x >= 'a' && x <= 'z' ||
				x >= 'A' && x <= 'Z' ||
				x >= '0' && x <= '9'
				=> c.toChar.toString

			case '!' | '#' | '$' | '&' | '+' | '-' | '.' | '^' | '_' | '`' | '|' | '~'
				=> c.toChar.toString

			case x
				=> "%%%02x" format (c & 0xff)
		}
}
