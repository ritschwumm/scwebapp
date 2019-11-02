package scwebapp

import scutil.lang._

object ChannelState {
	final case class Initial[T]()						extends ChannelState[T]
	final case class HasValue[T](value:T)				extends ChannelState[T]
	final case class HasHandler[T](handler:Effect[T])	extends ChannelState[T]
	final case class Final[T]()							extends ChannelState[T]
}

sealed trait ChannelState[T]
