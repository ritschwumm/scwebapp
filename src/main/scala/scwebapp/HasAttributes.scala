package scwebapp

trait HasAttributes {
	def attribute[T<:AnyRef](name:String):HttpAttribute[T]
}
