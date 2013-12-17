package scwebapp.servlet

import java.io._
import java.util.zip.GZIPOutputStream

import javax.servlet._
import javax.servlet.http._

import scutil.lang._
import scutil.implicits._
import scutil.io.URIComponent

import scwebapp._
import scwebapp.implicits._
import scwebapp.factory.mimeType._
import scwebapp.source._

// @see https://github.com/apache/tomcat/blob/trunk/java/org/apache/catalina/servlets/DefaultServlet.java
final class FileServlet extends HttpServlet {
	private var baseDir:File	= null

	@throws(classOf[ServletException])
	override  def init() {
		// TODO add flag for fullPathUTF8 instead of pathInfoUTF8
		val baseParam	=
				getServletConfig.initParameters firstString "basePath" getOrElse {
					throw new ServletException("missing init param basePath")
				}
		val basePath	=
				getServletContext realPath baseParam getOrElse {
					throw new ServletException("missing realpath for init param basePath")
				}
			
		baseDir	= new File(basePath)
		if (!baseDir.exists)		throw new ServletException(s"FileServlet init param 'basePath' value '${basePath}' does actually not exist in file system.")
		if (!baseDir.isDirectory) 	throw new ServletException(s"FileServlet init param 'basePath' value '${basePath}' is actually not a directory in file system.")
		if (!baseDir.canRead)		throw new ServletException(s"FileServlet init param 'basePath' value '${basePath}'  is actually not readable in file system.")
	}

	override protected def doHead(request:HttpServletRequest, response:HttpServletResponse) {
		processRequest(request, response, false)
	}

	override protected def doGet(request:HttpServletRequest, response:HttpServletResponse) {
		processRequest(request, response, true)
	}
	
	private def processRequest(request:HttpServletRequest, response:HttpServletResponse, content:Boolean) {
		val sourceOpt	=
				for {
					// TODO questionable
					path	<- request.pathInfoUTF8
					source	<- fileSource(path)
				}
				yield source
				
		val source	=
				sourceOpt getOrElse {
					response sendError HttpServletResponse.SC_NOT_FOUND
					return
				}
				
		new SourceHandler(source) apply request apply response
	}
	
	private def fileSource(path:String):Option[FileSource] =
			baseDir / (URIComponent decode path) guardBy { _.exists } map { file =>
				val mimeType	= getServletContext mimeTypeFor file.getName getOrElse application_octetStream
				new FileSource(file, mimeType)
			}
}
