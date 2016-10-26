package scwebapp

import java.io._
import java.nio.charset.Charset
import java.util.zip.GZIPOutputStream

import scala.annotation.tailrec

import scutil.lang._
import scutil.implicits._

import scwebapp.data.InclusiveRange

object HttpOutput {
	private val bufferSize = 16384
	
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
			
	// TODO toInt is questionable
	def writeByteArrayRange(data:Array[Byte], range:InclusiveRange):HttpOutput	=
			withOutputStream { ost =>
				ost write (data, range.start.toInt, range.length.toInt)
			}
			
	def writeFile(data:File):HttpOutput	=
			withOutputStream { ost =>
				new FileInputStream(data) use { ist =>
					ist transferTo ost
				}
			}
			
	def writeFileRange(data:File, range:InclusiveRange):HttpOutput	=
			withOutputStream { ost =>
				new RandomAccessFile(data, "r") use { input =>
					val buffer	= new Array[Byte](bufferSize)
					input seek range.start
					@tailrec
					def loop(todo:Long) {
						if (todo != 0) {
							val read	= input read (buffer, 0, (todo min buffer.length).toInt)
							if (read >= 0) {
								ost write (buffer, 0, read)
								loop(todo - read)
							}
						}
					}
					loop(range.length)
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
				val wr	= new OutputStreamWriter(ost, encoding)
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
