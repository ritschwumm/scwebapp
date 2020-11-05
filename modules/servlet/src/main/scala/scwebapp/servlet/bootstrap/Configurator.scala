package scwebapp.servlet.bootstrap

import scutil.core.implicits._
import scutil.lang._
import scutil.log._

object Configurator extends Logging {
	def configure[C](source:String=>Option[String], schema:Vector[Property[C]], initial:C):C	= {
		val states				= schema map (property(_, source))
		val (result, entries)	= states.sequenceState run initial
		entries foreach {
			case Note.Default(key, value)			=> INFO(key, "using default value", value)
			case Note.Change(key, value)			=> INFO(key, "configured to", value)
			case Note.Error(key, value, message)	=> ERROR(key, "invalid value", value, message)
		}
		entries foreach {
			case Note.Error(_,_,_)	=>	sys error "invalid configuration"
			case _					=>
		}
		result
	}

	@SuppressWarnings(Array("org.wartremover.warts.ToString"))
	def property[C](prop:Property[C], source:String=>Option[String]):State[C,Note]	=
		source(prop.key).cata[State[C,Note]](
			State { s => (s, Note.Default(prop.key, s.toString)) },
			raw => prop.mod(raw).cata[State[C,Note]](
				error	=> State { s => (s, 		Note.Error(prop.key, raw.toString, error)) },
				change	=> State { s => (change(s),	Note.Change(prop.key, if (prop.visible) raw.toString else "<redacted>")) }
			)
		)
}
