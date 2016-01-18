package scwebapp
package servlet

trait HttpAttribute[T] {
	def get:Option[T]
	def set(t:Option[T]):Unit
}
