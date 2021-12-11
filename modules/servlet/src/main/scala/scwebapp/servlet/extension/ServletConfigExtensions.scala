package scwebapp.servlet.extension

import jakarta.servlet.*

import scwebapp.data.*

object ServletConfigExtensions {
	extension(peer:ServletConfig) {
		def initParameters:CaseParameters	=
			CaseParameters.extract(peer.getInitParameterNames, peer.getInitParameter)
	}
}
