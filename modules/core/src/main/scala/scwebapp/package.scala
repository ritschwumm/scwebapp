package scwebapp

import scutil.lang.*

type HttpPredicate	= Predicate[HttpRequest]
type HttpHandler	= HttpRequest => HttpResponder
type HttpPHandler	= HttpRequest => Option[HttpResponder]

def HttpPredicate(it:HttpPredicate):HttpPredicate	= it
def HttpHandler(it:HttpHandler):HttpHandler			= it
def HttpPHandler(it:HttpPHandler):HttpPHandler		= it
