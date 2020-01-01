package scwebapp.servlet

import java.util.{ Collection=>JCollection }
import java.io.InputStream

import javax.servlet.http.Part

import scala.jdk.CollectionConverters._

import scutil.base.implicits._

import scwebapp._
import scwebapp.data._

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
