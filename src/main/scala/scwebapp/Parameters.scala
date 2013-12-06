package scwebapp

import scutil.implicits._

trait Parameters {
	def all:Seq[(String,String)]
	
	def names:Seq[String]
	
	def allString(name:String):Seq[String]	=
			all flatMap { case (k, v) =>
				if (matchName(k, name))	Seq(v)
				else					Seq.empty
			}
	
	def firstString(name:String):Option[String]
	
	def firstInt(name:String):Option[Int]	=
			firstString(name) flatMap { _.toIntOption }
		
	def firstLong(name:String):Option[Long]	=
			firstString(name) flatMap { _.toLongOption }
		
	def firstDate(name:String):Option[HttpDate]	=
			firstString(name) flatMap HttpDateFormat.parse
		
	//------------------------------------------------------------------------------
	
	protected def caseSensitive:Boolean
		
	protected def matchName(a:String, b:String):Boolean	=
			if (caseSensitive)	a == b
			else				a equalsIgnoreCase b
}
