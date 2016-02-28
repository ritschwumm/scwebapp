package scwebapp.factory

import java.nio.charset.Charset

import scwebapp.data._

object mimeType extends mimeType

trait mimeType {
	val text_plain					= MimeType("text",			"plain")
	val text_html					= MimeType("text",			"html")
	val text_javascript				= MimeType("text",			"javascript")
	val text_css					= MimeType("text",			"css")
	val application_javascript		= MimeType("application",	"javascript")
	val application_json			= MimeType("application",	"json")
	val application_octetStream		= MimeType("application",	"octet-stream")
	// NOTE this doesn't officially exist, but some browsers do support it
	val application_forceDownload	= MimeType("application",	"force-download")
	val application_form			= MimeType("application",	"x-www-form-urlencoded")
	val audio_mpeg					= MimeType("audio",			"mpeg")
	val image_jpeg					= MimeType("image",			"jpeg")
	val multipart_byteranges		= MimeType("multipart",		"byteranges")
	
	def text_plain_charset(encoding:Charset):MimeType	=
			addCharset(text_plain, encoding)
		
	def text_html_charset(encoding:Charset):MimeType	=
			addCharset(text_html, encoding)
		
	def text_javascript_charset(encoding:Charset):MimeType	=
			addCharset(text_javascript, encoding)
		
	def text_css_charset(encoding:Charset):MimeType	=
			addCharset(text_css, encoding)
		
	private def addCharset(mimeType:MimeType, encoding:Charset):MimeType	=
			mimeType addParameter ("charset", encoding.name)
		
	def multipart_byteranges_boundary(boundary:String):MimeType	=
			multipart_byteranges addParameter ("boundary", boundary)
}
