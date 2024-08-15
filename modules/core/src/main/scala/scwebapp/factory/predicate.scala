package scwebapp.factory

import scutil.core.implicits.*

import scwebapp.*

object predicate {
	def Method(method:HttpMethod):HttpPredicate	=
		_.method.toOption.exists(_ == method)

	def ContextPath(path:String):HttpPredicate	=
		_.contextPath ==== path

	def ServletPath(path:String):HttpPredicate	=
		_.servletPath ==== path

	def FullPathRaw(path:String):HttpPredicate	=
		_.fullPath.raw ==== path

	def FullPathUTF8(path:String):HttpPredicate	=
		_.fullPath.utf8.exists(_ ==== path)

	def PathInfoRaw(path:String):HttpPredicate	=
		_.pathInfo.raw ==== path

	def PathInfoUTF8(path:String):HttpPredicate	=
		_.pathInfo.utf8.exists(_ ==== path)
}
