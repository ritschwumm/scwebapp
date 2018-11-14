package scwebapp.header

import scutil.base.implicits._
import scutil.lang._

import scwebapp.HeaderType

// TODO not typesafe

object CacheControl extends HeaderType[CacheControl] {
	val key	= "Cache-Control"

	def parse(it:String):Option[CacheControl]	=
			Some(CacheControl(it splitAroundChar ','))

	def unparse(it:CacheControl):String	=
			it.directives mkString ", "
}

final case class CacheControl(directives:ISeq[String])