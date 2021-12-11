package scwebapp.servlet

final class HttpAttribute[T<:AnyRef](getFunc:()=>AnyRef, setFunc:(AnyRef)=>Unit, removeFunc:()=>Unit) {
	@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
	def get:Option[T] = Option(getFunc().asInstanceOf[T])
	def set(t:Option[T]):Unit	= {
		t match {
			case Some(tt)	=> setFunc(tt)
			case None		=> removeFunc()
		}
	}
}
