package scwebapp.servlet.bootstrap

import java.io.File

import scutil.base.implicits._
import scutil.lang._

object Value {
	//------------------------------------------------------------------------------
	//## restricted primitives
	
	def positive(s:String):Tried[String,Int]	=
			integer(s) guardByOr (_ > 0, "must be positive")
		
	def between(low:Int, high:Int)(s:String):Tried[String,Int]	=
			integer(s) guardByOr (_ >= low, s"must be >= $low") guardByOr (_ <= high, s"must be <= $high")

	def nonEmpty(s:String):Tried[String,String]	=
			string(s) guardByOr (_.nonEmpty, "must not be empty")
		
	//------------------------------------------------------------------------------
	//## primitives
	
	def boolean(s:String):Tried[String,Boolean]	=
			nonEmpty(s) flatMap {
				case "true"		=> Win(true)
				case "false"	=> Win(false)
				case x			=> Fail("must be 'true' or 'false'")
			}
			
	def integer(s:String):Tried[String,Int]	=
			s.toIntOption toWin "must be a number"
		
	def string(s:String):Tried[String,String]	=
			Win(s)
			
	def file(s:String):Tried[String,File]	=
			nonEmpty(s) map { new File(_) }
}
