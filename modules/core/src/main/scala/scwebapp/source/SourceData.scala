package scwebapp.source

import java.io._

import scutil.time.MilliInstant

import scwebapp.HttpOutput
import scwebapp.data._

object SourceData {
	def httpDateContentId(lastModified:HttpDate, size:Long):String	=
			lastModified.seconds.toString + "-" + size.toString

	def milliInstantContentId(lastModified:MilliInstant, size:Long):String	=
		lastModified.millis.toString + "-" + size.toString

	def forFile(
		file:File,
		contentId:String,
		lastModified:MilliInstant,
		caching:Option[SourceCaching],
		mimeType:MimeType,
		disposition:Option[SourceDisposition],
		enableGZIP:Boolean
	):SourceData	=
			SourceData(
				size			= file.length,
				range			= HttpOutput writeFileRange (file, _),
				contentId		= contentId,
				lastModified	= HttpDate fromMilliInstant lastModified,
				caching			= caching,
				mimeType		= mimeType,
				disposition		= disposition,
				enableGZIP		= enableGZIP
			)
}

final case class SourceData(
	size:Long,
	range:InclusiveRange=>HttpOutput,
	contentId:String,
	lastModified:HttpDate,
	caching:Option[SourceCaching],
	mimeType:MimeType,
	disposition:Option[SourceDisposition],
	enableGZIP:Boolean
)
