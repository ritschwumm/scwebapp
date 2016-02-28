package scwebapp.data

import java.nio.charset.Charset

import scutil.io.Base64
import scutil.io.Charsets

import scwebapp.format._
import scwebapp.parser.string._

// @see http://www.ietf.org/rfc/rfc2617.txt
object BasicAuthorization {
	// TODO hardcoded and probably wrong
	private val encoding	= Charsets.utf_8
	
	//------------------------------------------------------------------------------
	
	lazy val parser:CParser[BasicAuthorization]	= parsers.value
		
	def unparse(it:BasicAuthorization):String	=
			"Basic " + (Base64 encode ((it.name getBytes encoding) ++ Array(':'.toByte) ++ (it.password getBytes encoding)))
		
	private object parsers {
		import HttpParsers._
		
		val userid:CParser[String]						= (TEXT filter { _ != ':' }).seq.stringify
		val password:CParser[String]					= TEXT.seq.stringify
		val basicCredentials:CParser[(String,String)]	= userid left cis(':') next password
		
		val base64Credentials:CParser[(String,String)]	= base64(encoding) nestString basicCredentials
		def basicAuthentication(charset:Charset):CParser[(String,String)]	=
				symbolN("Basic") right (base64Credentials eating LWSP)
		
		val value:CParser[BasicAuthorization]		= basicAuthentication(encoding) map (BasicAuthorization.apply _).tupled
	}
}

final case class BasicAuthorization(name:String, password:String)