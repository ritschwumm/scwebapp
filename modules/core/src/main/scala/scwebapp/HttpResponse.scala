package scwebapp

import scwebapp.status.*

final case class HttpResponse(
	status:HttpStatus			= OK,
	reason:Option[String]		= None,
	headers:Seq[HeaderValue]	= Vector.empty,
	body:HttpOutput				= HttpOutput.empty
)
