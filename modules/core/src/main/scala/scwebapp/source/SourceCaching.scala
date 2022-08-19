package scwebapp.source

import scwebapp.data.*

enum SourceCaching {
	case Silent
	case Disabled
	case Expires(when:HttpDate=>HttpDate)
}
