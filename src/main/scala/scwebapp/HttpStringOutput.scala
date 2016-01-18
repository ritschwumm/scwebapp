package scwebapp

import java.io._
//import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._

trait HttpStringOutput {
	def writer(effect:Effect[Writer]):HttpOutput
	
	final def string(data:Thunk[String]):HttpOutput	=
			writer { wr =>
				val string	= data()
				wr write string
			}
			
	final def fullString(data:String):HttpOutput	=
			string(thunk(data))
			
	//------------------------------------------------------------------------------
	
	final def fromReader(data:Thunk[Reader]):HttpOutput	=
			writer { wr =>
				val input	= data()
				input use { rd =>
					rd transferTo wr
				}
			}
}
