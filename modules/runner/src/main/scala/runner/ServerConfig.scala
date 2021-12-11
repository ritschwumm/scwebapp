package scwebapp.runner

import java.net.*

import scutil.core.implicits.*
import scutil.lang.*
import scutil.time.*

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
