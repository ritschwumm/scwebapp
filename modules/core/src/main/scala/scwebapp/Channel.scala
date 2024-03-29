package scwebapp

import scutil.lang.*

// NOTE this what cats-effect calls a Deferred
final class Channel[T] {
	private val state	= Synchronized[ChannelState[T]](ChannelState.Initial())
	private val ok		= thunk(())

	/** must not be called more than once */
	def put(v:T):Unit	= state.modifyState(putter(v)).apply()

	/** must not be called more than once */
	def get(h:Effect[T]):Unit	= state.modifyState(getter(h)).apply()

	private def putter(v:T):State[ChannelState[T],Thunk[Unit]]	=
		State {
			case ChannelState.Initial()		=> ChannelState.HasValue(v)		-> ok
			case ChannelState.HasHandler(h)	=> ChannelState.Final()			-> thunk(h(v))
			case old						=> old							-> thunk(sys error "cannot put twice")
		}

	private def getter(h:Effect[T]):State[ChannelState[T],Thunk[Unit]]	=
		State {
			case ChannelState.Initial()		=> ChannelState.HasHandler(h)	-> ok
			case ChannelState.HasValue(v)	=> ChannelState.Final()			-> thunk(h(v))
			case old						=> old							-> thunk(sys error "cannot get twice")
		}
}
