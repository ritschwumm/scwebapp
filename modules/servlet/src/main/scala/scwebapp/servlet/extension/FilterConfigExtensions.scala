package scwebapp.servlet.extension

import jakarta.servlet.*

import scwebapp.data.*

object FilterConfigExtensions {
	extension(peer:FilterConfig) {
		def initParameters:CaseParameters	=
			CaseParameters.extract(peer.getInitParameterNames, peer.getInitParameter)
	}
}
