package scwebapp.runner

import scutil.base.implicits._

object InetPort {
	val lowest	= 0
	val highest	= 65535
}

final case class InetPort(value:Int) {
	import InetPort._
	require(value >= lowest,	so"must be >= ${lowest.toString}")
	require(value <= highest,	so"must be <= ${highest.toString}")
}
