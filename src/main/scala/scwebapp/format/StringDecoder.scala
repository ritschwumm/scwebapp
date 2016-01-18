package scwebapp
package format

import java.nio.ByteBuffer
import java.nio.charset._

import scutil.lang._

object StringDecoder {
	def decodeOption(charset:Charset):Array[Byte]=>Option[String]	=
			decode(charset) andThen { _.toOption }
		
	/** rejects any errors in the byte array */
	def decode(charset:Charset):Array[Byte]=>Tried[CharacterCodingException,String]	=
			bytes	=> {
				// new String(bytes, charset)
				val decoder	= charset.newDecoder
				decoder onMalformedInput		CodingErrorAction.REPORT
				decoder onUnmappableCharacter	CodingErrorAction.REPORT
				try {
					Win((decoder decode (ByteBuffer wrap bytes)).toString)
				}
				catch { case e:CharacterCodingException =>
					Fail(e)
				}
			}
}
