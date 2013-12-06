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
	
	def headers:Parameters	=
			new Parameters {
				def caseSensitive:Boolean	= true
				
				def all:Seq[(String,String)]	=
						for {	
							name	<- names
							value	<- (peer getHeaders name).asInstanceOf[JCollection[String]].asScala
						}
						yield (name, value)
						
				def names:Seq[String]	=
						peer.getHeaderNames.asInstanceOf[JCollection[String]].asScala.toVector
					
				def firstString(name:String):Option[String] = 
						Option(peer getHeader name)
			}
		
	//------------------------------------------------------------------------------
	//## stream
	
	// TODO handle exceptions
	
	def asString(encoding:Charset):String	=
			withReader(encoding) { _.readFully }
		
	def asStringUTF8:String	=
			asString(utf_8)
		
	def withReader[T](encoding:Charset)(func:Reader=>T):T	=
			new InputStreamReader(openInputStream(), encoding) use func
			
	def withInputStream[T](func:InputStream=>T):T	=
			openInputStream() use func
			
	def openInputStream():InputStream	=
			peer.getInputStream
			
	//------------------------------------------------------------------------------
	//## special headers
	
	def contentType:Option[MimeType]	=
			MimeType parse peer.getContentType
		
	def contentDisposition:Option[String]	=
			headers firstString "Content-Disposition"
			
	// TODO real parser
	// does not check for "attachment" or "inline"
	// does not distinguish between missing header and invalid format
	def fileName:Seq[String]	=
			for {
				header			<- contentDisposition.toVector
				snip			<- header splitAroundChar ';'
				(name, value)	<- snip.trim splitAroundFirst '='
				if name == "filename"
			}
			// @see http://tools.ietf.org/html/rfc2184 for non-ascii
			yield value replaceAll ("^\"|\"$", "")
}
