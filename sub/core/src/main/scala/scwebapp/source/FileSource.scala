package scwebapp.source

import java.io._

import scala.annotation.tailrec

import scutil.implicits._
import scutil.time.MilliInstant

import scwebapp.data._

object FileSource {
	private val bufferSize = 16384
	
	def simple(file:File, mimeType:MimeType):FileSource	=
			new FileSource(
				peer			= file,
				fileName		= file.getName,
				lastModified	= file.lastModifiedMilliInstant,
				mimeType		= mimeType
			)
}

final class FileSource(peer:File, val fileName:String, val lastModified:MilliInstant, val mimeType:MimeType) extends Source {
	def size:Long	= peer.length
	
	def range(start:Long, size:Long):SourceRange	=
			// TODO handle exceptions?
			SourceRange { output =>
				new RandomAccessFile(peer, "r") use { input =>
					val buffer	= new Array[Byte](FileSource.bufferSize)
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
