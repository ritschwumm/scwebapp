package scwebapp
package pimp

import java.util.{ Collection=>JCollection }
import java.io._
import java.nio.charset.Charset

import javax.servlet.http.Part

import scala.collection.JavaConverters._

import scutil.lang._
import scutil.implicits._
import scutil.io.Charsets
import scutil.time.MilliInstant

import scwebapp.HttpInput

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
			.map { it =>
				MimeType parse it toWin s"invalid content type ${it}" 
			}
			.sequenceTried
			
	/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
	def encoding:Tried[String,Option[Charset]]	=
			contentType.toOption.flatten cata (
				Win(None),
				contentType => {
					(contentType.parameters firstString "charset")
					.map { it => 
						Charsets byName it mapFail constant(s"invalid charset ${it}") 
					}
					.sequenceTried
				}
			)
		
	def contentDisposition:Option[String]	=
			headers firstString "Content-Disposition"
	
	// @see https://www.ietf.org/rfc/rfc2047.txt
	def fileName:Tried[String,Option[String]]	=
			contentDisposition
			.map { it:String =>
				(HttpParser parseContentDisposition it)
				.flatMap	{ _._2 firstString "filename" }
				.toWin		(s"invalid content disposition ${it}")
			}
			.sequenceTried
			
	//------------------------------------------------------------------------------
	//## body
	
	def body:HttpInput	=
			new HttpInput {
				def inputStream[T](handler:InputStream=>T):T	= handler(peer.getInputStream)
			}
}
