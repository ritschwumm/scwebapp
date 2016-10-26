package scwebapp.source

import java.io._

import scutil.lang._
import scutil.time.MilliInstant

import scwebapp.HttpOutput
import scwebapp.data._

object SourceData {
	def simpleContentId(lastModified:MilliInstant, size:Long):String	=
			lastModified.millis.toString + "-" + size.toString
	
	def forFile(
		file:File,
		contentId:String,
		lastModified:MilliInstant,
		expires:Option[Endo[HttpDate]],
		mimeType:MimeType,
		disposition:Option[SourceDisposition],
		enableGZIP:Boolean
	):SourceData	=
			SourceData(
				size			= file.length,
				range			= HttpOutput writeFileRange (file, _),
				contentId		= contentId,
				lastModified	= lastModified,
				expires			= expires,
				mimeType		= mimeType,
				disposition		= disposition,
				enableGZIP		= enableGZIP
			)
}

final case class SourceData(
	size:Long,
	range:InclusiveRange=>HttpOutput,
	contentId:String,
	lastModified:MilliInstant,
	// Some to keep stuff in the cache for some time
	expires:Option[Endo[HttpDate]],
	mimeType:MimeType,
	disposition:Option[SourceDisposition],
	enableGZIP:Boolean
)
