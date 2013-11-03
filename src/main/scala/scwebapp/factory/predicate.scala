package scwebapp
package factory

import java.nio.charset.Charset

import scutil.Implicits._

import scwebapp.implicits._
import scwebapp.method._

object predicate extends predicate 

trait predicate {
	def Method(method:HttpMethod):HttpPredicate	=
			_.getMethod.toUpperCase ==== method.id.toUpperCase
			
	def FullPath(path:String, encoding:Charset):HttpPredicate	=
			it => (it fullPath encoding) ==== path
			
	def FullPathRaw(path:String):HttpPredicate	=
			_.fullPathRaw ==== path
		
	def PathInfo(path:String, encoding:Charset):HttpPredicate	=
			_ pathInfo encoding exists { _ ==== path }
		
	def PathInfoRaw(path:String):HttpPredicate	=
			_.pathInfoRaw exists { _ ==== path }
			
	def ServletPath(path:String):HttpPredicate	=
			_.getServletPath ==== path
}
