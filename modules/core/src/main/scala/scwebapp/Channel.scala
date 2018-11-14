package scwebapp

import scutil.lang._

object Channel {
	private sealed trait ChannelState[T]
	private final case class Initial[T]()						extends ChannelState[T]
	private final case class HasValue[T](value:T)				extends ChannelState[T]
	private final case class HasHandler[T](handler:Effect[T])	extends ChannelState[T]
	private final case class Final[T]()							extends ChannelState[T]
}

final class Channel[T] {
	import Channel._

	private val state	= Synchronized[ChannelState[T]](Initial())
	private val ok		= thunk(())

	/** must not be called more than once */
	def put(v:T) {
		state modify putter(v) apply ()
	}

	/** must not be called more than once */
	def get(h:Effect[T]) {
		state modify getter(h) apply ()
	}

	private def putter(v:T):State[ChannelState[T],Thunk[Unit]]	=
			State {
				case Initial()		=> HasValue(v)		-> ok
				case HasHandler(h)	=> Final()			-> thunk(h(v))
				case old			=> old				-> thunk(sys error "cannot put twice")
			}

	private def getter(h:Effect[T]):State[ChannelState[T],Thunk[Unit]]	=
			State {
				case Initial()		=> HasHandler(h)	-> ok
				case HasValue(v)	=> Final()			-> thunk(h(v))
				case old			=> old				-> thunk(sys error "cannot get twice")
			}
}
