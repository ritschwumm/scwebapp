package scwebapp.servlet

trait HasAttributes {
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]
}
