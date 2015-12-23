package scwebapp

import java.io._
import java.nio.charset.Charset

import scutil.implicits._

trait HttpInput {
	def inputStream[T](handler:InputStream=>T):T
	
	final def byteArray[T](handler:Array[Byte]=>T):T	=
			inputStream { it => handler(it.readFully) }
		
	final def fullByteArray:Array[Byte]	=
			byteArray(identity)
		
	final def encoded(encoding:Charset):HttpStringInput	=
			new HttpStringInput {
				def reader[T](handler:Reader=>T):T	=
						inputStream { ist =>
							handler(new InputStreamReader(ist, encoding))
						}
			}
}
