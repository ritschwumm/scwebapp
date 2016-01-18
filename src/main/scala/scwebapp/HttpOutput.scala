package scwebapp

import java.io._
import java.nio.charset.Charset
import java.util.zip.GZIPOutputStream

import scutil.lang._
import scutil.implicits._

object HttpOutput {
	// TODO hardcoded
	private val gzipBufferSize	= 8192
	
	//------------------------------------------------------------------------------
	
	def outputStream(effect:Effect[OutputStream]):HttpOutput	=
			new HttpOutput {
				def transferTo(ost:OutputStream):Unit	= effect(ost)
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

trait HttpOutput { self =>
	def transferTo(ost:OutputStream):Unit
	
	final def gzip:HttpOutput	=
			new HttpOutput {
				def transferTo(ost:OutputStream) {
					val stream	= new GZIPOutputStream(ost, HttpOutput.gzipBufferSize)
					self transferTo stream
					stream.finish()
				}
			}
}
