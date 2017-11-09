package scwebapp.runner

import java.net._

import scutil.lang._

object ServerConfig {
	val L	= TLens.Gen[ServerConfig]
	
	val default	=
			ServerConfig(
				host	= None,
				port	= InetPort(8080),
				path	= ContextPath("/")
			)
}

final case class ServerConfig(
	host:Option[InetAddress],
	port:InetPort,
	path:ContextPath
) {
	// null means InetAddress.anyLocalAddress()
	val bindAdr		= new InetSocketAddress(host.orNull, port.value)
}
