package scwebapp
package pimp

import java.util.{ Collection=>JCollection }
import java.io._
import java.nio.charset.Charset

import javax.servlet.http.Part

import scala.collection.JavaConverters._

import scutil.Implicits._
import scutil.io.Charsets.utf_8
import scutil.time.MilliInstant

object PartImplicits extends PartImplicits

trait PartImplicits {
	implicit def extendPart(peer:Part):PartExtension	= 
			new PartExtension(peer)
}

final class PartExtension(peer:Part) {
	def contentType:Option[MimeType]	=
			MimeType parse peer.getContentType
	
	//------------------------------------------------------------------------------
	
	def asString(encoding:Charset):String	=
			new InputStreamReader(peer.getInputStream, encoding) use { _ readFully }
		
	def asStringUTF8:String	=
			asString(utf_8)
		
	//------------------------------------------------------------------------------
	
	def headers:Seq[(String,String)]	=
			for {	
				name	<- headerNames
				value	<- (peer getHeaders name).asInstanceOf[JCollection[String]].asScala
			}
			yield (name, value)
			
	def headerNames:Seq[String]	=
			peer.getHeaderNames.asInstanceOf[JCollection[String]].asScala.toVector
		
	def headerString(name:String):Option[String] = 
			Option(peer getHeader name)
			
	def headerInt(name:String):Option[Int] = 
			headerString(name) flatMap { _.toIntOption }
			
	def headerLong(name:String):Option[Long] = 
			headerString(name) flatMap { _.toLongOption }
		
	def headerHttpDate(name:String):Option[HttpDate]	=
			headerString(name) flatMap { HttpDateFormat.parse }
		
	//------------------------------------------------------------------------------
	
	def fileName:Seq[String]	=
			for {
				header			<- headerString("Content-Disposition").toVector
				snip			<- header splitAroundChar ';'
				(name, value)	<- snip.trim splitAroundFirst '='
				if name == "filename"
			}
			// @see http://tools.ietf.org/html/rfc2184 for non-ascii
			yield value replaceAll ("^\"|\"$", "")
}
