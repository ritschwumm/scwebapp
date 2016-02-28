package scwebapp.source

import java.io._

import scutil.implicits._
import scutil.time.MilliInstant

import scwebapp.HttpOutput
import scwebapp.data._

object Source {
	//  entityId could be lastModified.millis.toString + "-" + size.toString
	def forFile(entityId:String, file:File, lastModified:Option[MilliInstant], disposition:Option[(ContentDispositionType,Option[String])],  mimeType:MimeType, enableGZIP:Boolean):Source	=
			Source(
				entityId		= entityId,
				
				lastModified	= lastModified getOrElse file.lastModifiedMilliInstant,
				size			= file.length,
				range			= HttpOutput writeFileRange (file, _),
				
				mimeType		= mimeType,
				disposition		= disposition,
				enableGZIP		= enableGZIP
			)
}

case class Source(
	// used for caching
	entityId:String,
	
	lastModified:MilliInstant,
	size:Long,
	range:InclusiveRange=>HttpOutput,
	
	mimeType:MimeType,
	
	disposition:Option[(ContentDispositionType,Option[String])],
	enableGZIP:Boolean
)
