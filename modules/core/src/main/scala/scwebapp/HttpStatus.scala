package scwebapp

sealed abstract class HttpStatus(val id:Int)

package status {
	//------------------------------------------------------------------------------
	//## informational

	case object CONTINUE						extends HttpStatus(100)
	case object SWITCHING_PROTOCOLS				extends HttpStatus(101)

	/*
	102	processing
	*/

	/*
	//## warnings

	110	response
	111	revalidation
	112	disconnected
	113	heuristic
	119	miscellaneous
	214	transformation
	299	miscellaneous persistent
	*/

	//------------------------------------------------------------------------------
	//## success

	case object OK								extends HttpStatus(200)
	case object CREATED							extends HttpStatus(201)
	case object ACCEPTED						extends HttpStatus(202)
	case object NON_AUTHORITATIVE_INFORMATION	extends HttpStatus(203)
	case object NO_CONTENT						extends HttpStatus(204)
	case object RESET_CONTENT					extends HttpStatus(205)
	case object PARTIAL_CONTENT					extends HttpStatus(206)

	/*
	207	multi
	*/

	//------------------------------------------------------------------------------
	//## redirection

	case object MULTIPLE_CHOICES				extends HttpStatus(300)
	case object MOVED_PERMANENTLY				extends HttpStatus(301)
	case object MOVED_TEMPORARILY				extends HttpStatus(302)
	// case object FOUND							extends HttpStatus(302)
	case object SEE_OTHER						extends HttpStatus(303)
	case object NOT_MODIFIED					extends HttpStatus(304)
	case object USE_PROXY						extends HttpStatus(305)
	case object TEMPORARY_REDIRECT				extends HttpStatus(307)

	//------------------------------------------------------------------------------
	//## client error

	case object BAD_REQUEST						extends HttpStatus(400)
	case object UNAUTHORIZED					extends HttpStatus(401)
	case object PAYMENT_REQUIRED				extends HttpStatus(402)
	case object FORBIDDEN						extends HttpStatus(403)
	case object NOT_FOUND						extends HttpStatus(404)
	case object METHOD_NOT_ALLOWED				extends HttpStatus(405)
	case object NOT_ACCEPTABLE					extends HttpStatus(406)
	case object PROXY_AUTHENTICATION_REQUIRED	extends HttpStatus(407)
	case object REQUEST_TIMEOUT					extends HttpStatus(408)
	case object CONFLICT						extends HttpStatus(409)
	case object GONE							extends HttpStatus(410)
	case object LENGTH_REQUIRED					extends HttpStatus(411)
	case object PRECONDITION_FAILED				extends HttpStatus(412)
	case object REQUEST_ENTITY_TOO_LARGE		extends HttpStatus(413)
	case object REQUEST_URI_TOO_LONG			extends HttpStatus(414)
	case object UNSUPPORTED_MEDIA_TYPE			extends HttpStatus(415)
	case object REQUESTED_RANGE_NOT_SATISFIABLE	extends HttpStatus(416)
	case object EXPECTATION_FAILED				extends HttpStatus(417)

	/*
	421	too many connections
	422	unprocessable entity
	423	locked
	424	failed dependency
	425	unordered collection
	426	upgrade required
	449	retry with
	*/

	//------------------------------------------------------------------------------
	//## server error

	case object INTERNAL_SERVER_ERROR			extends HttpStatus(500)
	case object NOT_IMPLEMENTED					extends HttpStatus(501)
	case object BAD_GATEWAY						extends HttpStatus(502)
	case object SERVICE_UNAVAILABLE				extends HttpStatus(503)
	case object GATEWAY_TIMEOUT					extends HttpStatus(504)
	case object HTTP_VERSION_NOT_SUPPORTED		extends HttpStatus(505)

	/*
	506	variant also negotiates
	507	insufficient storage
	509	bandwidth limit exceeded
	510	not extended
	530	user access denied
	*/
}
