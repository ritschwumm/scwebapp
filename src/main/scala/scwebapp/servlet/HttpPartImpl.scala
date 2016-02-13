package scwebapp.servlet

import java.util.{ Collection=>JCollection }
import java.io.InputStream
import java.nio.charset.Charset

import javax.servlet.http.Part

import scala.collection.JavaConverters._

import scutil.implicits._
import scutil.io.Charsets

import scwebapp._
import scwebapp.data._

private final class HttpPartImpl(peer:Part) extends HttpPart {
	def name:String	= peer.getName
	def size:Long	= peer.getSize
	
	def headers:HttpHeaders	=
			HttpHeaders(
				NoCaseParameters(
					for {	
						name	<- peer.getHeaderNames.asInstanceOf[JCollection[String]].asScala.toVector
						value	<- (peer getHeaders name).asInstanceOf[JCollection[String]].asScala
					}
					yield name	-> value
				)
			)
	
	def body:HttpInput	=
			new HttpInput {
				def withInputStream[T](handler:InputStream=>T):T	=
						peer.getInputStream use handler
			}
}
