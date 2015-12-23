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
		val mimeTypeFor	= request.getServletContext mimeTypeFor _
		val responder	=
				for {
					path		<- request.pathInfoUTF8				toWin	SetStatus(NOT_FOUND)
					file		= baseDir / (URIComponent decode path)
					safeFile	<- file guardBy safeToDeliver		toWin	SetStatus(NOT_FOUND)
					mimeType	= mimeTypeFor(safeFile.getName) getOrElse application_octetStream
					handler		=
							new SourceHandler(
								source			= FileSource simple (safeFile, mimeType),
								enableInline	= mimeType.major ==== "image",
								enableGZIP		= mimeType.major ==== "text"
							)
				}
				yield handler apply request
				
		responder.merge
	}
			
	private def safeToDeliver(file:File):Boolean	=
			file.exists && file.isFile && file.canRead &&
			(baseDir containsRecursive file).isDefined
}
