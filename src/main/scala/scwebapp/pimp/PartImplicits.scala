package scwebapp
package pimp

import java.util.{ Collection=>JCollection }
import java.io._
import java.nio.charset.Charset

import javax.servlet.http.Part

import scala.collection.JavaConverters._

import scutil.lang._
import scutil.implicits._
import scutil.io.Charsets.utf_8
import scutil.time.MilliInstant

object PartImplicits extends PartImplicits

trait PartImplicits {
	implicit def extendPart(peer:Part):PartExtension	= 
			new PartExtension(peer)
}

final class PartExtension(peer:Part) {
	//------------------------------------------------------------------------------
	//## headers
	
	def headers:NoCaseParameters	=
			NoCaseParameters(
				for {	
					name	<- peer.getHeaderNames.asInstanceOf[JCollection[String]].asScala.toVector
					value	<- (peer getHeaders name).asInstanceOf[JCollection[String]].asScala
				}
				yield name	-> value
			)
		
	//------------------------------------------------------------------------------
	//## special headers
	
	/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
	def contentType:Tried[String,Option[MimeType]]	=
			peer.getContentType.guardNotNull
			.map { name => MimeType parse name toWin name }
			.sequenceTried
		
	def contentDisposition:Option[String]	=
			headers firstString "Content-Disposition"
			
	// TODO real parser, @see MimeType.scala
	// does not check for "attachment" or "inline"
	// does not distinguish between missing header and invalid format
	def fileName:Seq[String]	=
			for {
				header			<- contentDisposition.toVector
				snip			<- header splitAroundChar ';'
				(name, value)	<- snip.trim splitAroundFirst '='
				if name == "filename"
			}
			yield HttpUtil unquote value
			
	//------------------------------------------------------------------------------
	//## body
	
	// TODO Part can have a character encoding, but the API doesn't tell us about it
	
	def body:HttpBody	=
			new HttpBody {
				def inputStream()	= peer.getInputStream
			}
}
