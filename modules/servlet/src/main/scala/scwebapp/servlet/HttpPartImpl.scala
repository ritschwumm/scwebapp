package scwebapp.servlet

import java.util.{ Collection as JCollection }
import java.io.InputStream

import jakarta.servlet.http.Part

import scala.jdk.CollectionConverters.*

import scutil.core.implicits.*

import scwebapp.*
import scwebapp.data.*

private final class HttpPartImpl(peer:Part) extends HttpPart {
	def name:String	= peer.getName
	def size:Long	= peer.getSize

	@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
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
			def withInputStream[T](handler:InputStream=>T):T	= peer.getInputStream use handler
		}
}
