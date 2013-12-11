package scwebapp

import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset

import scutil.implicits._
import scutil.io.Charsets.utf_8

// TODO handle exceptions

trait HttpBody {
	def inputStream():InputStream
	
	//------------------------------------------------------------------------------
	
	def withReader[T](encoding:Charset)(func:Reader=>T):T	=
			new InputStreamReader(inputStream(), encoding) use func
			
	def withInputStream[T](func:InputStream=>T):T	=
			inputStream() use func
		
	//------------------------------------------------------------------------------
		
	def readString(encoding:Charset):String	=
			withReader(encoding) { _.readFully }
		
	def readStringUTF8:String	=
			readString(utf_8)
}
