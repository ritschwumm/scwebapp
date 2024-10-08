package scwebapp.data

import scala.compiletime.asMatchable

import java.util.{ Enumeration as JEnumeration }

import scutil.jdk.implicits.*

import scwebapp.format.CaseUtil

sealed trait Parameters {
	def all:Seq[(String,String)]
	def names:Set[String]
	def get(name:String):Seq[String]

	//------------------------------------------------------------------------------

	def firstString(name:String):Option[String]	=
		get(name).headOption

	def firstInt(name:String):Option[Int]	=
		firstString(name).flatMap(_.toIntOption)

	def firstLong(name:String):Option[Long]	=
		firstString(name).flatMap(_.toLongOption)

	def firstDate(name:String):Option[HttpDate]	=
		firstString(name).flatMap(HttpDate.parse)
}

//------------------------------------------------------------------------------

object CaseParameters {
	val empty	= CaseParameters(Vector.empty)

	def apply(values:Seq[(String,String)]):CaseParameters	=
		new CaseParameters(values)

	@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
	def extract(names:JEnumeration[String], value:String=>String):CaseParameters	=
		CaseParameters(
			names.asInstanceOf[JEnumeration[String]].toIterator.toVector.map{ name =>
				name -> value(name)
			}
		)
}

/** case sensitive */
final class CaseParameters(values:Seq[(String,String)]) extends Parameters {
	def all:Seq[(String,String)]	=
		values

	def names:Set[String]	=
		values.map(_._1).toSet

	def get(name:String):Seq[String]	=
		values.collect	{ case (`name`, value) => value }

	def append(name:String, value:String):CaseParameters	=
		new CaseParameters(values :+ (name -> value))

	override def equals(that:Any):Boolean	=
		that.asMatchable match {
			case x:CaseParameters	=> this.all == x.all
			case _					=> false
		}

	override def toString:String	=
		"CaseParameters(" + values.map{ (k, v) => k + "=" + v }.mkString(", ") + ")"
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
		values.map { (k,v) => (CaseUtil.lowerCase(k), v) }

	def names:Set[String]	=
		values.map{ it => CaseUtil.lowerCase(it._1) }.toSet

	def get(name:String):Seq[String]	=
		values.collect	{
			case (k, v) if (CaseUtil.lowerCase(k)) == (CaseUtil.lowerCase(name))	=> v
		}

	def append(name:String, value:String):NoCaseParameters	=
		new NoCaseParameters(values :+ (name -> value))

	override def equals(that:Any):Boolean	=
		that.asMatchable match {
			case x:NoCaseParameters	=> this.all == x.all
			case _					=> false
		}

	override def toString:String	=
		"NoCaseParameters(" + values.map { (k, v) => k + "=" + v }.mkString(", ") + ")"
}
