package scwebapp.runner

import java.net._

import scutil.core.implicits._
import scutil.lang._
import scutil.time._

object ServerConfig {
	val L	= Lens.Gen[ServerConfig]

	val default	=
		ServerConfig(
			host		= None,
			port		= InetPort(8080),
			path		= ContextPath("/"),
			idleTimeout	= 30000.millis
		)
}

final case class ServerConfig(
	host:Option[InetAddress],
	port:InetPort,
	path:ContextPath,
	idleTimeout:MilliDuration
) {
	// null means InetAddress.anyLocalAddress()
	val bindAdr		= new InetSocketAddress(host.orNull, port.value)
}
