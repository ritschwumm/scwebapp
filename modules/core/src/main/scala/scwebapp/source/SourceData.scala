package scwebapp.source

import java.nio.file.*

import scutil.time.MilliInstant

import scwebapp.HttpOutput
import scwebapp.data.*

object SourceData {
	def httpDateContentId(lastModified:HttpDate, size:Long):String	=
		lastModified.seconds.toString + "-" + size.toString

	def milliInstantContentId(lastModified:MilliInstant, size:Long):String	=
		lastModified.millis.toString + "-" + size.toString

	def forFile(
		file:Path,
		contentId:String,
		lastModified:MilliInstant,
		caching:SourceCaching,
		mimeType:MimeType,
		disposition:Option[SourceDisposition],
		enableGZIP:Boolean
	):SourceData	=
		SourceData(
			size			= Files.size(file),
			range			= HttpOutput.writeFileRange(file, _),
			contentId		= contentId,
			lastModified	= HttpDate.fromMilliInstant(lastModified),
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
	caching:SourceCaching,
	mimeType:MimeType,
	disposition:Option[SourceDisposition],
	enableGZIP:Boolean
)
