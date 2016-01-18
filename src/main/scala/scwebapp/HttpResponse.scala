package scwebapp

import scutil.lang._

import scwebapp.status._

case class HttpResponse(
	status:HttpStatus				= OK,
	reason:Option[String]			= None,
	headers:ISeq[(String,String)]	= Vector.empty,
	body:HttpOutput					= HttpOutput.empty
)
