package scwebapp
package factory

import java.nio.charset.Charset

import scutil.implicits._

import scwebapp.implicits._

object predicate extends predicate 

trait predicate {
	def Method(method:HttpMethod):HttpPredicate	=
			_.getMethod.toUpperCase ==== method.id.toUpperCase
			
	def FullPathRaw(path:String):HttpPredicate	=
			_.fullPathRaw ==== path
	
	def FullPathServlet(path:String):HttpPredicate	=
			_.fullPathServlet ==== path
	
	def FullPathUTF8(path:String):HttpPredicate	=
			_.fullPathUTF8 ==== path
			
	def PathInfoRaw(path:String):HttpPredicate	=
			_.pathInfoRaw exists { _ ==== path }
			
	def PathInfoServlet(path:String):HttpPredicate	=
			_.pathInfoServlet exists { _ ==== path }
		
	def PathInfoUTF8(path:String):HttpPredicate	=
			_.pathInfoUTF8 exists { _ ==== path }
		
	def ServletPath(path:String):HttpPredicate	=
			_.getServletPath ==== path
}
