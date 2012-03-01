package scwebapp

object StandardMimeTypes {
	val text_plain				= MimeType("text",			"plain")
	val text_html				= MimeType("text",			"html")
	val text_javascript			= MimeType("text",			"javascript")
	val application_json		= MimeType("application",	"json")
	val application_octetStream	= MimeType("application",	"octet-stream")
	val audio_mpeg				= MimeType("audio",			"mpeg")
	val image_jpeg				= MimeType("image",			"jpeg")
	
	val unknown_unknown			= MimeType("*",				"*")
}
