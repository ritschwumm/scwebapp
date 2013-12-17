package scwebapp.source

import java.io._

import scala.annotation.tailrec

import scutil.implicits._
import scutil.time.MilliInstant

import scwebapp.MimeType

object FileSource {
	private val DEFAULT_BUFFER_SIZE = 16384
}

class FileSource(peer:File, val mimeType:MimeType) extends Source {
	def name:String				= peer.getName
	def size:Long				= peer.length
	def modified:MilliInstant	= peer.lastModifiedMilliInstant
	
	def range(start:Long, size:Long)	= new SourceRange {
		// TODO handle exceptions?
		def transferTo(output:OutputStream) =
				new RandomAccessFile(peer, "r") use { input =>
					val buffer	= new Array[Byte](FileSource.DEFAULT_BUFFER_SIZE)
					input seek start
					@tailrec
					def loop(todo:Long) {
						if (todo != 0) {
							val read	= input read (buffer, 0, (todo min buffer.length).toInt)
							if (read >= 0) {
								output write (buffer, 0, read)
								loop(todo - read)
							}
						}
					}
					loop(size)
				}
	}
}
