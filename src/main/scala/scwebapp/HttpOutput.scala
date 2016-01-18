package scwebapp

import java.io._
import java.nio.charset.Charset
import java.util.zip.GZIPOutputStream

import scutil.lang._
import scutil.implicits._

object HttpOutput {
	val empty:HttpOutput	=
			withOutputStream(constant(()))
		
	def concat(its:ISeq[HttpOutput]):HttpOutput	=
			(its foldLeft empty)(_ ~> _)
		
	//------------------------------------------------------------------------------
	
	def withOutputStream(handler:Effect[OutputStream]):HttpOutput	=
			new HttpOutput {
				def intoOutputStream(ost:OutputStream):Unit	= handler(ost)
			}
			
	def writeByteArray(data:Array[Byte]):HttpOutput	=
			withOutputStream { ost =>
				ost write (data, 0, data.length)
			}
			
	def writeFile(data:File):HttpOutput	=
			withOutputStream { ost =>
				new FileInputStream(data) use { ist =>
					ist transferTo ost
				}
			}
			
	def pipeInputStream(data:Thunk[InputStream]):HttpOutput	=
			withOutputStream { ost =>
				data() use { ist =>
					ist transferTo ost
				}
			}
			
	//------------------------------------------------------------------------------
	
	def withWriter(encoding:Charset)(handler:Effect[Writer]):HttpOutput	=
			withOutputStream { ost =>
				val wr	= new OutputStreamWriter(ost)
				handler(wr)
				wr.flush()
			}
			
	def writeString(encoding:Charset, data:String):HttpOutput	=
			withWriter(encoding)(_ write data)
	
	def pipeReader(encoding:Charset, data:Thunk[Reader]):HttpOutput	=
			withWriter(encoding) { wr =>
				data() use { rd =>
					rd transferTo wr
				}
			}
}

trait HttpOutput {
	def intoOutputStream(ost:OutputStream):Unit
	
	final def ~> (that:HttpOutput):HttpOutput	=
			HttpOutput withOutputStream { ost =>
				this intoOutputStream ost
				that intoOutputStream ost
			}
	
	final def gzip(bufferSize:Int):HttpOutput	=
			HttpOutput withOutputStream { ost =>
				val stream	= new GZIPOutputStream(ost, bufferSize)
				intoOutputStream(stream)
				stream.finish()
			}
}
