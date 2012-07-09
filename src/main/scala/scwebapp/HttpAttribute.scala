package scwebapp

import javax.servlet._
import javax.servlet.http._

import scutil.Functions._

object HttpAttribute {
	def servlet[T<:AnyRef](context:ServletContext, name:String)	= new HttpAttribute[T](
			()	=> (context getAttribute name).asInstanceOf[T],
			t	=> context setAttribute (name, t),
			()	=> context removeAttribute name)
			
	def session[T<:AnyRef](context:HttpSession, name:String)	= new HttpAttribute[T](
			()	=> (context getAttribute name).asInstanceOf[T],
			t	=> context setAttribute (name, t),
			()	=> context removeAttribute name)
			
	def request[T<:AnyRef](context:HttpServletRequest, name:String)	= new HttpAttribute[T](
			()	=> (context getAttribute name).asInstanceOf[T],
			t	=> context setAttribute (name, t),
			()	=> context removeAttribute name)
}

final class HttpAttribute[T](getter:Thunk[T], setter:Effect[T], remover:Task) {
	def get:Option[T] = Option(getter())
	
	def set(t:Option[T]) { 
		t match {
			case Some(tt)	=> setter(tt)
			case None		=> remover()
		}
	}
}
