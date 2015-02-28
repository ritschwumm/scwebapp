package scwebapp

import java.io._
import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._

object HttpOutput {
	def outputStream(effect:Effect[OutputStream]):HttpOutput	=
			new HttpOutput {
				def transferTo(ost:OutputStream)	= effect(ost)
			}
			
	def byteArray(data:Thunk[Array[Byte]]):HttpOutput	=
			outputStream { ost =>
				val bytes	= data()
				ost write (bytes, 0, bytes.length)
			}
			
	def fullByteArray(data:Array[Byte]):HttpOutput	=
			byteArray(thunk(data))
			
	def encoded(encoding:Charset):HttpStringOutput	=
			new HttpStringOutput {
				def writer(effect:Effect[Writer]):HttpOutput	=
						outputStream { ost =>
							effect(new OutputStreamWriter(ost, encoding))
						}
			}
			
	//------------------------------------------------------------------------------
	
	def fromFile(data:Thunk[File]):HttpOutput	=
			outputStream { ost =>
				val file	= data()
				new FileInputStream(file) use { ist =>
					ist transferTo ost
				}
			}
	
	def fromInputStream(data:Thunk[InputStream]):HttpOutput	=
			outputStream { ost =>
				val input	= data()
				input use { ist =>
					ist transferTo ost
				}
			}
}

trait HttpOutput {
	def transferTo(ost:OutputStream)	
}

trait HttpStringOutput {
	def writer(effect:Effect[Writer]):HttpOutput
	
	final def string(data:Thunk[String]):HttpOutput	=
			writer { wr =>
				val string	= data()
				wr write string
			}
			
	def fullString(data:String):HttpOutput	=
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
