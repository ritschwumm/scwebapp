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

	val DisableCaching:HeaderValues	=
			Vector[HeaderValue](
				CacheControl(ISeq("no-cache", "no-store", "must-revalidate")),
				Pragma("no-cache"),
				Expires(HttpDate.zero)
			)

	@deprecated("use DisableCaching", "0.200.0")
	val NoCache:HeaderValues	= DisableCaching
}
