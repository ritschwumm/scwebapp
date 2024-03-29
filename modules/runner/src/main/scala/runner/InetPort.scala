package scwebapp.runner

import scutil.core.implicits.*

object InetPort {
	val lowest	= 0
	val highest	= 65535
}

final case class InetPort(value:Int) {
	import InetPort.*
	require(value >= lowest,	show"must be >= $lowest")
	require(value <= highest,	show"must be <= $highest")
}
