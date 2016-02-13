package scwebapp

import scutil.lang._

import scwebapp.status._

case class HttpResponse(
	status:HttpStatus			= OK,
	reason:Option[String]		= None,
	headers:ISeq[HeaderValue]	= Vector.empty,
	body:HttpOutput				= HttpOutput.empty
)
