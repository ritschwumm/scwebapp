package scwebapp.source

import scutil.time.MilliInstant

import scwebapp.MimeType

trait Source {
	def name:String
	def size:Long
	def modified:MilliInstant
	def mimeType:MimeType
	def range(start:Long, size:Long):SourceRange
}
