package scwebapp.servlet

import javax.servlet._
import javax.servlet.http._

object HasAttributesImplicits extends HasAttributesImplicits

trait HasAttributesImplicits {
	implicit def ServletRequestHasAttributes(peer:ServletRequest):HasAttributes	=
		new HasAttributesImpl(peer.getAttribute, peer.setAttribute, peer.removeAttribute)

	implicit def HttpSessionHasAttributes(peer:HttpSession):HasAttributes	=
		new HasAttributesImpl(peer.getAttribute, peer.setAttribute, peer.removeAttribute)

	implicit def ServletContextHasAttributes(peer:ServletContext):HasAttributes	=
		new HasAttributesImpl(peer.getAttribute, peer.setAttribute, peer.removeAttribute)

	private final class HasAttributesImpl(getFunc:String=>AnyRef, setFunc:(String,AnyRef)=>Unit, removeFunc:String=>Unit) extends HasAttributes {
		def attribute[T<:AnyRef](name:String):HttpAttribute[T]	=
			new HttpAttribute[T] {
				@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
				def get:Option[T] = Option(getFunc(name).asInstanceOf[T])
				def set(t:Option[T]):Unit	= {
					t match {
						case Some(tt)	=> setFunc(name, tt)
						case None		=> removeFunc(name)
					}
				}
			}
	}
}
