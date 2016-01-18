package scwebapp

import java.nio.charset.Charset

import scutil.lang._

import scwebapp.data._
import scwebapp.format._

trait HttpPart {
	def name:String
	def size:Long
	def headers:NoCaseParameters
	def body:HttpInput
	
	//------------------------------------------------------------------------------
	
	final def contentType:Tried[String,Option[MimeType]]	=
			HeaderParser contentType headers
			
	final def encoding:Tried[String,Option[Charset]]	=
			HeaderParser encoding headers
		
	final def contentDisposition:Tried[String,Option[Disposition]]	=
			HeaderParser contentDisposition headers
			
	// @see https://www.ietf.org/rfc/rfc2047.txt
	final def fileName:Tried[String,Option[String]]	=
			HeaderParser fileName headers
}
