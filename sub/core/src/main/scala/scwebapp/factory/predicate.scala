package scwebapp.factory

import java.nio.charset.Charset

import scutil.implicits._

import scwebapp._

object predicate extends predicate

trait predicate {
	def Method(method:HttpMethod):HttpPredicate	=
			_.method.toOption == Some(method)

	def ContextPath(path:String):HttpPredicate	=
			_.contextPath ==== path

	def ServletPath(path:String):HttpPredicate	=
			_.servletPath ==== path

	def FullPathRaw(path:String):HttpPredicate	=
			_.fullPathRaw ==== path
	
	def FullPathUTF8(path:String):HttpPredicate	=
			_.fullPathUTF8 ==== path
		
	def PathInfoRaw(path:String):HttpPredicate	=
			_.pathInfoRaw ==== path
		
	def PathInfoUTF8(path:String):HttpPredicate	=
			_.pathInfoUTF8 ==== path
}