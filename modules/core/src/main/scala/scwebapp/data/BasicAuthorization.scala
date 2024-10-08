package scwebapp.data

import java.nio.charset.Charset

import scutil.lang.implicits.*
import scutil.lang.*
import scutil.codec.*

import scwebapp.format.*
import scparse.ng.text.*

// @see http://www.ietf.org/rfc/rfc2617.txt
object BasicAuthorization {
	// TODO hardcoded and probably wrong
	private val encoding	= Charsets.utf_8

	//------------------------------------------------------------------------------

	lazy val parser:TextParser[BasicAuthorization]	= parsers.value

	def unparse(it:BasicAuthorization):String	=
		"Basic " + (
			Base64.encodeByteString(
				it.name.toByteString(encoding)	.concat(
				ByteString.one(':'))			.concat(
				it.password.toByteString(encoding))
			)
		)

	private object parsers {
		import HttpParsers.*

		val COLON	= TextParser.is(':')

		val userid:TextParser[String]						= COLON.not.right(TEXT).seq.stringify
		val password:TextParser[String]						= TEXT.seq.stringify
		val basicCredentials:TextParser[(String,String)]	= userid.left(COLON).next(password)

		val base64Credentials:TextParser[(String,String)]	= base64(encoding).nestString(basicCredentials.phrase)
		def basicAuthentication(charset:Charset):TextParser[(String,String)]	=
			symbolN("Basic").right(base64Credentials.eatLeft(LWSP))

		val value:TextParser[BasicAuthorization]		= basicAuthentication(encoding).map(BasicAuthorization.apply.tupled)
	}
}

final case class BasicAuthorization(name:String, password:String)
