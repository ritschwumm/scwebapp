package scwebapp

import java.io._
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

import scutil.core.implicits._
import scutil.lang._

object HttpInput {
	def outofInputStream(ist:Thunk[InputStream]):HttpInput	=
			new HttpInput {
				def withInputStream[T](handler:InputStream=>T):T	= handler(ist())
			}
}

trait HttpInput { self =>
	def withInputStream[T](handler:InputStream=>T):T
	
	final def readByteString():ByteString	=
			ByteString unsafeFromByteArray readByteArray()
		
	final def readByteArray():Array[Byte]	=
			withInputStream { _.readFully }
		
	//------------------------------------------------------------------------------
		
	final def withReader[T](encoding:Charset)(handler:Reader=>T):T	=
			withInputStream { ist =>
				handler(new InputStreamReader(ist, encoding))
			}
	
	final def readString(encoding:Charset):String	=
			withReader(encoding) { _.readFully }
		
	//------------------------------------------------------------------------------
		
	final def gunzip(bufferSize:Int):HttpInput	=
			new HttpInput {
				def withInputStream[T](handler:InputStream=>T):T	=
						self withInputStream { ist =>
							handler(new GZIPInputStream(ist, bufferSize))
						}
			}
}
