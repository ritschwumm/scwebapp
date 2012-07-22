package scwebapp

import scwebapp.api._
import scwebapp.syntax._

object HttpImplicits extends HttpImplicits

trait HttpImplicits 
		extends ApiImplicits
		with 	SyntaxImplicits
