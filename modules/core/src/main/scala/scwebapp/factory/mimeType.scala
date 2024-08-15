package scwebapp.factory

import java.nio.charset.Charset

import scutil.core.implicits.*

import scwebapp.data.*

object mimeType {
	//------------------------------------------------------------------------------
	//## text

	val text_plain		= text("plain",			None)
	val text_html		= text("html",			None)
	val text_javascript	= text("javascript",	None)
	val text_css		= text("css",			None)

	def text_plain_charset(charset:Charset):MimeType		= text("plain",			Some(charset))
	def text_html_charset(charset:Charset):MimeType			= text("html",			Some(charset))
	def text_javascript_charset(charset:Charset):MimeType	= text("javascript",	Some(charset))
	def text_css_charset(charset:Charset):MimeType			= text("css",			Some(charset))

	def text(minor:String, charset:Option[Charset]):MimeType	=
		MimeType("text", minor,
			NoCaseParameters(charset.toVector.map { it =>
				("charset" -> it.name)
			})
		)

	//------------------------------------------------------------------------------
	//## application

	val application_javascript		= MimeType("application",	"javascript")
	val application_json			= MimeType("application",	"json")
	val application_octetStream		= MimeType("application",	"octet-stream")
	// NOTE this doesn't officially exist, but some browsers do support it
	val application_forceDownload	= MimeType("application",	"force-download")
	val application_form			= MimeType("application",	"x-www-form-urlencoded")

	//------------------------------------------------------------------------------
	//## media

	val audio_mpeg	= MimeType("audio",	"mpeg")
	val image_jpeg	= MimeType("image",	"jpeg")

	//------------------------------------------------------------------------------
	//## multipart

	val multipart_byteranges	= MimeType("multipart",	"byteranges")

	def multipart_byteranges_boundary(boundary:String):MimeType	=
		multipart_byteranges.addParameter("boundary", boundary)

	val multipart_formData		= MimeType("multipart",	"form-data")

	def multipart_formData_boundary(boundary:String):MimeType	=
		multipart_formData.addParameter("boundary", boundary)
}
