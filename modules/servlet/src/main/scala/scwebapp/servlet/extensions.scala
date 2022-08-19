package scwebapp.servlet

import scwebapp.servlet.extension.*

// TODO why is this necessary?
@SuppressWarnings(Array("org.wartremover.warts.Overloading"))
object extensions {
	export FilterConfigExtensions.*
	export HttpSessionExtensions.*
	export ServletConfigExtensions.*
	export ServletContextExtensions.*
	export ServletRequestExtensions.*
}
