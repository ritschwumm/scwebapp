package scwebapp

object HttpMethod {
	import method.*

	val all:Seq[HttpMethod]	=
		Vector[HttpMethod](
			OPTIONS, HEAD, GET, POST, PUT, DELETE, TRACE, CONNECT,
			PROPFIND, PROPPATCH, MKCOL, COPY, MOVE, LOCK, UNLOCK
		)

	def lookup(caseless:String):Option[HttpMethod]	=
		all find { _.id == caseless }
}

sealed abstract class HttpMethod(val id:String)

package method {
	// http
	case object OPTIONS		extends HttpMethod("OPTIONS")
	case object HEAD		extends HttpMethod("HEAD")
	case object GET			extends HttpMethod("GET")
	case object POST		extends HttpMethod("POST")
	case object PUT			extends HttpMethod("PUT")
	case object DELETE		extends HttpMethod("DELETE")
	case object TRACE		extends HttpMethod("TRACE")
	case object CONNECT		extends HttpMethod("CONNECT")

	// webdav
	case object PROPFIND	extends HttpMethod("PROPFIND")
	case object PROPPATCH	extends HttpMethod("PROPPATCH")
	case object MKCOL		extends HttpMethod("MKCOL")
	case object COPY		extends HttpMethod("COPY")
	case object MOVE		extends HttpMethod("MOVE")
	case object LOCK		extends HttpMethod("LOCK")
	case object UNLOCK		extends HttpMethod("UNLOCK")
}
