package scwebapp

import java.io.*
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

import scutil.jdk.implicits.*
import scutil.lang.*

object HttpInput {
	def ofInputStream(ist:Thunk[InputStream]):HttpInput	=
		new HttpInput {
			def withInputStream[T](handler:InputStream=>T):T	= handler(ist())
		}
}

trait HttpInput { self =>
	def withInputStream[T](handler:InputStream=>T):T

	final def readByteString():ByteString	=
		withInputStream { _.readFullyByteString() }

	//------------------------------------------------------------------------------

	final def withReader[T](encoding:Charset)(handler:Reader=>T):T	=
		withInputStream { ist =>
			handler(new InputStreamReader(ist, encoding))
		}

	final def readString(encoding:Charset):String	=
		withReader(encoding) { _.readFully() }

	//------------------------------------------------------------------------------

	final def gunzip(bufferSize:Int):HttpInput	=
		new HttpInput {
			def withInputStream[T](handler:InputStream=>T):T	=
				self withInputStream { ist =>
					handler(new GZIPInputStream(ist, bufferSize))
				}
		}
}
