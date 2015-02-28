package scwebapp

import scwebapp.factory._

object instances extends instances

trait instances
		extends	handler
		with	phandler
		with	predicate
		with	responder
		with	docType
		with	mimeType
