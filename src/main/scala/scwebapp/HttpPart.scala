package scwebapp

import java.nio.charset.Charset

import scutil.lang._

import scwebapp.format._

trait HttpPart {
	def name:String
	def size:Long
	def headers:NoCaseParameters
	def body:HttpInput
	
	//------------------------------------------------------------------------------
	
	def contentType:Tried[String,Option[MimeType]]	=
			HeaderParser contentType headers
			
	def encoding:Tried[String,Option[Charset]]	=
			HeaderParser encoding headers
		
	def contentDisposition:Tried[String,Option[Disposition]]	=
			HeaderParser contentDisposition headers
			
	// @see https://www.ietf.org/rfc/rfc2047.txt
	def fileName:Tried[String,Option[String]]	=
			HeaderParser fileName headers
}
