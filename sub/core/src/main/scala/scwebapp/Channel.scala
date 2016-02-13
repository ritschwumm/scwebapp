package scwebapp

import scutil.lang._

object Channel {
	private sealed trait State[T]
	private case class Initial[T]()						extends State[T]
	private case class HasValue[T](value:T)				extends State[T]
	private case class HasHandler[T](handler:Effect[T])	extends State[T]
	private case class Final[T]()						extends State[T]
}

final class Channel[T] {
	import Channel._
	
	private val state	= Synchronized[State[T]](Initial())
	private val ok		= task(())
	
	/** must not be called more than once */
	def put(v:T) {
		state
		.modify {
			_ match {
				case Initial()		=> HasValue(v)		-> ok
				case HasHandler(h)	=> Final()			-> task(h(v))
				case old			=> old				-> task(sys error "cannot put twice")
			}
		}
		.apply()
	}
	
	/** must not be called more than once */
	def get(h:Effect[T]) {
		state
		.modify {
			_ match {
				case Initial()		=> HasHandler(h)	-> ok
				case HasValue(v)	=> Final()			-> task(h(v))
				case old			=> old				-> task(sys error "cannot get twice")
			}
		}
		.apply()
	}
}
