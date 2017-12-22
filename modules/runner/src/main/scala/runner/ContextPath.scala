package scwebapp.runner

final case class ContextPath(value:String) {
	require(value startsWith "/",					"must start with a /")
	require(value == "/" || !(value endsWith "/"),	"must not end with a /")
}
