package scwebapp.factory

import scutil.lang._

import scwebapp.HeaderValue
import scwebapp.header._
import scwebapp.data._

object header extends header

trait header {
	// type helper
	type HeaderValues	= ISeq[HeaderValue]
	def HeaderValues(values:HeaderValue*):ISeq[HeaderValue]	= values.toVector

	def NoCache:HeaderValues	=
			Vector[HeaderValue](
				CacheControl(ISeq("no-cache", "no-store", "must-revalidate")),
				Pragma("no-cache"),
				Expires(HttpDate.zero)
			)
}
