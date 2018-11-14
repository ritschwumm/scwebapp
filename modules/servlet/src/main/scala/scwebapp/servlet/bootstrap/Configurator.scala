package scwebapp.servlet.bootstrap

import scutil.base.implicits._
import scutil.lang._
import scutil.log._

object Configurator extends Logging {
	def configure[C](source:PFunction[String,String], schema:Vector[Property[C]], initial:C):C	= {
		val states				= schema map (property(_, source))
		val (result, entries)	= states.sequenceState run initial
		entries foreach {
			case NoteDefault(key, value)		=> INFO(key, "using default value", value)
			case NoteChange(key, value)			=> INFO(key, "configured to", value)
			case NoteError(key, value, message)	=> ERROR(key, "invalid value", value, message)
		}
		entries foreach {
			case NoteError(_,_,_)	=>	sys error "invalid configuration"
			case _					=>
		}
		result
	}

	def property[C](prop:Property[C], source:PFunction[String,String]):State[C,Note]	=
			source apply prop.key cata (
				State { s => (s, NoteDefault(prop.key, s.toString)) },
				raw => prop mod raw cata (
					error	=> State { s => (s, 		NoteError(prop.key, raw.toString, error)) },
					change	=> State { s => (change(s),	NoteChange(prop.key, if (prop.visible) raw.toString else "<redacted>")) }
				)
			)
}
