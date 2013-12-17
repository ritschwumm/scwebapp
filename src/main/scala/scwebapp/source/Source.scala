package scwebapp.source

import scutil.time.MilliInstant

import scwebapp.MimeType

trait Source {
	def fileName:String
	def lastModified:MilliInstant
	def mimeType:MimeType
	def size:Long
	def range(start:Long, size:Long):SourceRange
}
