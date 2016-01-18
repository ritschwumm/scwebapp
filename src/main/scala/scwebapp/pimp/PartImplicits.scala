package scwebapp
package pimp

import java.util.{ Collection=>JCollection }
import java.nio.charset.Charset

import javax.servlet.http.Part

import scutil.lang._
import scutil.implicits._

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
					name	<- peer.getHeaderNames.asInstanceOf[JCollection[String]].toIterable.toVector
					value	<- (peer getHeaders name).asInstanceOf[JCollection[String]].toIterable
				}
				yield name	-> value
			)
		
	def contentType:Tried[String,Option[MimeType]]	=
			// NOTE this used peer.getContentType.guardNotNull
			HeaderParsers contentType headers
			
	def encoding:Tried[String,Option[Charset]]	=
			HeaderParsers encoding headers
			
	def contentDisposition:Tried[String,Option[ContentDisposition]]	=
			HeaderParsers contentDisposition headers
	
	def fileName:Tried[String,Option[String]]	=
			HeaderParsers fileName headers
			
	//------------------------------------------------------------------------------
	//## body
	
	def body:HttpInput	= HttpInput outofInputStream (thunk { peer.getInputStream })
}
