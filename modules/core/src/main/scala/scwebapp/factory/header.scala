package scwebapp.factory

import scwebapp.HeaderValue
import scwebapp.header.*
import scwebapp.data.*

object header {
	// type helper
	type HeaderValues	= Seq[HeaderValue]
	def HeaderValues(values:HeaderValue*):Seq[HeaderValue]	= values.toVector

	val DisableCaching:HeaderValues	=
		Vector[HeaderValue](
			CacheControl(Seq("no-cache", "no-store", "must-revalidate")),
			Pragma("no-cache"),
			Expires(HttpDate.zero)
		)
}
