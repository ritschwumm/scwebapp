package scwebapp

import scutil.lang._

final class HttpAttribute[T](getter:Thunk[T], setter:Effect[T], remover:Task) {
	def get:Option[T] = Option(getter())
	
	def set(t:Option[T]) { 
		t match {
			case Some(tt)	=> setter(tt)
			case None		=> remover()
		}
	}
}
