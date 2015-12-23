package scwebapp

import java.io.Reader

import scutil.implicits._

trait HttpStringInput {
	def reader[T](handler:Reader=>T):T
	
	final def string[T](handler:String=>T):T	=
			reader { it => handler(it.readFully) }
		
	final def fullString:String	=
			string(identity)
}
