package scwebapp.servlet

import java.util.{ Enumeration=>JEnumeration }

import javax.servlet._

import scutil.jdk.implicits._

import scwebapp.data._

object HasInitParametersImplicits extends HasInitParametersImplicits

trait HasInitParametersImplicits {
	implicit def ServletContextHasInitParameters(peer:ServletContext):HasInitParameters	=
		new HasInitParametersImpl(peer.getInitParameterNames, peer.getInitParameter)

	implicit def ServletConfigHasInitParameters(peer:ServletConfig):HasInitParameters	=
		new HasInitParametersImpl(peer.getInitParameterNames, peer.getInitParameter)

	implicit def FilterConfigHasInitParameters(peer:FilterConfig):HasInitParameters	=
		new HasInitParametersImpl(peer.getInitParameterNames, peer.getInitParameter)

	private final class HasInitParametersImpl(names: =>JEnumeration[_], value:String=>String) extends HasInitParameters {
		@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
		def initParameters:CaseParameters	=
			CaseParameters(
				for {
					name	<- names.asInstanceOf[JEnumeration[String]].toIterator.toVector
				}
				yield name -> value(name)
			)
	}
}
