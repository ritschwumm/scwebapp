package scwebapp.handler

import java.io._

import javax.servlet.http._

import scutil.implicits._
import scutil.io.URIComponent

import scwebapp._
import scwebapp.implicits._
import scwebapp.status._
import scwebapp.factory.mimeType._
import scwebapp.factory.responder._
import scwebapp.source._

// @see https://github.com/apache/tomcat/blob/trunk/java/org/apache/catalina/servlets/DefaultServlet.java
final class FileHandler(baseDir:File) extends HttpHandler {
	def apply(request:HttpServletRequest):HttpResponder	= {
		val file	= baseDir / request.pathInfoUTF8
		if (safeToDeliver(file)) {
			val mimeType	= request mimeTypeFor file.getName getOrElse application_octetStream
			val handler		=
					new SourceHandler(
						source			= FileSource simple (file, mimeType),
						enableInline	= mimeType.major ==== "image",
						enableGZIP		= mimeType.major ==== "text"
					)
			handler apply request
		}
		else {
			SetStatus(NOT_FOUND)
		}
	}
			
	private def safeToDeliver(file:File):Boolean	=
			file.exists && file.isFile && file.canRead &&
			(baseDir containsRecursive file).isDefined
}
