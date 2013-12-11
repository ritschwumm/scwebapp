package scwebapp

import scutil.implicits._

sealed trait Parameters {
	def all:Seq[(String,String)]
	def names:Set[String]
	def get(name:String):Seq[String]
	
	//------------------------------------------------------------------------------
	
	def firstString(name:String):Option[String]	=
			get(name).headOption
	
	def firstInt(name:String):Option[Int]	=
			firstString(name) flatMap { _.toIntOption }
		
	def firstLong(name:String):Option[Long]	=
			firstString(name) flatMap { _.toLongOption }
		
	def firstDate(name:String):Option[HttpDate]	=
			firstString(name) flatMap HttpDateFormat.parse
}

//------------------------------------------------------------------------------

object CaseParameters {
	val empty	= CaseParameters(Vector.empty)
	
	def apply(values:Seq[(String,String)]):CaseParameters	= 
			new CaseParameters(values)
}

/** case sensitive */
final class CaseParameters(values:Seq[(String,String)]) extends Parameters {
	def all:Seq[(String,String)]	=
			values
		
	def names:Set[String]	=
			(values map { _._1 }).toSet
		
	def get(name:String):Seq[String]	=
			values collect { case (`name`, value) => value }
			
	def append(name:String, value:String):CaseParameters	=
			new CaseParameters(values :+ (name -> value))
		
	override def equals(that:Any):Boolean	=
			that match {
				case x:CaseParameters	=> this.all == x.all
				case _					=> false
			}
			
	override def toString:String	=
			"CaseParameters(" + (values map { case (k, v) => k + "=" + v } mkString " ,") + ")"
}

//------------------------------------------------------------------------------

object NoCaseParameters {
	val empty	= NoCaseParameters(Vector.empty)
	
	def apply(values:Seq[(String,String)]):NoCaseParameters	= 
			new NoCaseParameters(values)
}

/** case insensitive */
final class NoCaseParameters(values:Seq[(String,String)]) extends Parameters {
	def all:Seq[(String,String)]	=
			values map { case (k,v) => (k.toLowerCase, v) }
		
	def names:Set[String]	=
			(values map { _._1.toLowerCase }).toSet
		
	def get(name:String):Seq[String]	=
			values flatMap { case (k, v) =>
				if (k equalsIgnoreCase name)	Vector(v)
				else							Vector.empty
			}
			
	def append(name:String, value:String):NoCaseParameters	=
			new NoCaseParameters(values :+ (name -> value))
		
	override def equals(that:Any):Boolean	=
			that match {
				case x:NoCaseParameters	=> this.all == x.all
				case _					=> false
			}
		
	override def toString:String	=
			"NoCaseParameters(" + (values map { case (k, v) => k + "=" + v } mkString " ,") + ")"
}
