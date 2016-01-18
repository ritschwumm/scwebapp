package scwebapp
package servlet

trait HasAttributes {
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]
}
