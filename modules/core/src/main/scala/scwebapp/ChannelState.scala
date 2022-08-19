package scwebapp

import scutil.lang.*

enum ChannelState[T] {
	case Initial[T]()						extends ChannelState[T]
	case HasValue[T](value:T)				extends ChannelState[T]
	case HasHandler[T](handler:Effect[T])	extends ChannelState[T]
	case Final[T]()							extends ChannelState[T]
}
