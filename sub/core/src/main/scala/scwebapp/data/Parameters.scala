package scwebapp.data

import scutil.lang._
import scutil.implicits._

import scwebapp.format.CaseUtil

sealed trait Parameters {
	def all:ISeq[(String,String)]
	def names:Set[String]
	def get(name:String):ISeq[String]
	
	//------------------------------------------------------------------------------
	
	def firstString(name:String):Option[String]	=
			get(name).headOption
	
	def firstInt(name:String):Option[Int]	=
			firstString(name) flatMap { _.toIntOption }
		
	def firstLong(name:String):Option[Long]	=
			firstString(name) flatMap { _.toLongOption }
		
	def firstDate(name:String):Option[HttpDate]	=
			firstString(name) flatMap HttpDate.parse
}

//------------------------------------------------------------------------------

object CaseParameters {
	val empty	= CaseParameters(Vector.empty)
	
	def apply(values:ISeq[(String,String)]):CaseParameters	=
			new CaseParameters(values)
}

/** case sensitive */
final class CaseParameters(values:ISeq[(String,String)]) extends Parameters {
	def all:ISeq[(String,String)]	=
			values
		
	def names:Set[String]	=
			(values map { _._1 }).toSet
		
	def get(name:String):ISeq[String]	=
			values collect	{ case (`name`, value) => value }
			
	def append(name:String, value:String):CaseParameters	=
			new CaseParameters(values :+ (name -> value))
		
	override def equals(that:Any):Boolean	=
			that match {
				case x:CaseParameters	=> this.all == x.all
				case _					=> false
			}
			
	override def toString:String	=
			"CaseParameters(" + (values map { case (k, v) => k + "=" + v } mkString ", ") + ")"
}

//------------------------------------------------------------------------------

object NoCaseParameters {
	val empty	= NoCaseParameters(Vector.empty)
	
	def apply(values:ISeq[(String,String)]):NoCaseParameters	=
			new NoCaseParameters(values)
}

/** case insensitive */
final class NoCaseParameters(values:ISeq[(String,String)]) extends Parameters {
	def all:ISeq[(String,String)]	=
			values map { case (k,v) => (CaseUtil lowerCase k, v) }
		
	def names:Set[String]	=
			(values map { it => CaseUtil lowerCase it._1 }).toSet
		
	def get(name:String):ISeq[String]	=
			values collect	{
				case (k, v) if (CaseUtil lowerCase k) == (CaseUtil lowerCase name)	=> v
			}
			
	def append(name:String, value:String):NoCaseParameters	=
			new NoCaseParameters(values :+ (name -> value))
		
	override def equals(that:Any):Boolean	=
			that match {
				case x:NoCaseParameters	=> this.all == x.all
				case _					=> false
			}
		
	override def toString:String	=
			"NoCaseParameters(" + (values map { case (k, v) => k + "=" + v } mkString ", ") + ")"
}
