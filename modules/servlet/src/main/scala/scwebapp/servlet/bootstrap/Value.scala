package scwebapp.servlet.bootstrap

import java.io.File

import scutil.core.implicits.*

object Value {
	//------------------------------------------------------------------------------
	//## restricted primitives

	def positive(s:String):Either[String,Int]	=
		integer(s).rightByOr(_ > 0, "must be positive")

	def between(low:Int, high:Int)(s:String):Either[String,Int]	=
		integer(s).rightByOr(_ >= low, show"must be >= $low").rightByOr(_ <= high, show"must be <= $high")

	def nonEmpty(s:String):Either[String,String]	=
		string(s).rightByOr(_.nonEmpty, "must not be empty")

	//------------------------------------------------------------------------------
	//## primitives

	def boolean(s:String):Either[String,Boolean]	=
		nonEmpty(s) flatMap {
			case "true"		=> Right(true)
			case "false"	=> Right(false)
			case x			=> Left("must be 'true' or 'false'")
		}

	def integer(s:String):Either[String,Int]	=
		s.toIntOption toRight "must be a number"

	def string(s:String):Either[String,String]	=
		Right(s)

	def file(s:String):Either[String,File]	=
		nonEmpty(s) map { new File(_) }
}
