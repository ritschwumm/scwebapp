package scwebapp

import java.io.*
import java.nio.file.Path
import java.nio.charset.Charset
import java.util.zip.GZIPOutputStream

import scala.annotation.tailrec

import scutil.core.implicits.*
import scutil.jdk.implicits.*
import scutil.lang.*
import scutil.lang.tc.Monoid
import scutil.io.*

import scwebapp.data.InclusiveRange

object HttpOutput {
	private val bufferSize = 16384

	val empty:HttpOutput	=
		withOutputStream(constant(()))

	def combineAll(its:Iterable[HttpOutput]):HttpOutput	=
		(its foldLeft empty)(_ `combine` _)

	given Monoid[HttpOutput]	= Monoid.instance(empty, _ `combine` _)

	//------------------------------------------------------------------------------

	def withOutputStream(handler:Effect[OutputStream]):HttpOutput	=
		new HttpOutput {
			def intoOutputStream(ost:OutputStream):Unit	= handler(ost)
		}

	def writeByteString(data:ByteString):HttpOutput	=
		withOutputStream { ost =>
			ost.writeByteString(data, 0, data.size)
		}

	def writeByteStringRange(data:ByteString, range:InclusiveRange):HttpOutput	=
		withOutputStream { ost =>
			ost.writeByteString(data, range.start.toInt, range.length.toInt)
		}

	def writeFile(data:Path):HttpOutput	=
		withOutputStream { ost =>
			MoreFiles.withInputStream(data) { ist =>
				ist.transferTo(ost)
			}
		}

	def writeFileRange(data:Path, range:InclusiveRange):HttpOutput	=
		withOutputStream { ost =>
			new RandomAccessFile(data.toFile, "r") use { input =>
				val buffer	= new Array[Byte](bufferSize)
				input.seek(range.start)
				@tailrec
				def loop(todo:Long):Unit	= {
					if (todo != 0) {
						val read	= input.read(buffer, 0, todo.min(buffer.length).toInt)
						if (read >= 0) {
							ost.write(buffer, 0, read)
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
				ist.transferTo(ost)
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
		withWriter(encoding)(_.write(data))

	def pipeReader(encoding:Charset, data:Thunk[Reader]):HttpOutput	=
		withWriter(encoding) { wr =>
			data() use { rd =>
				rd.transferTo(wr)
			}
		}
}

trait HttpOutput {
	def intoOutputStream(ost:OutputStream):Unit

	final def ~> (that:HttpOutput):HttpOutput	= combine(that)

	final def combine(that:HttpOutput):HttpOutput	=
		HttpOutput withOutputStream { ost =>
			this.intoOutputStream(ost)
			that.intoOutputStream(ost)
		}

	final def gzip(bufferSize:Int):HttpOutput	=
		HttpOutput withOutputStream { ost =>
			val stream	= new GZIPOutputStream(ost, bufferSize)
			intoOutputStream(stream)
			stream.finish()
		}
}
