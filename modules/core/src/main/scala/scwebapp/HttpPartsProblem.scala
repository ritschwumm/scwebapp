package scwebapp

import java.io.IOException

enum HttpPartsProblem {
	case NotMultipart(e:Exception)
	case InputOutputFailed(e:IOException)
	case SizeLimitExceeded(e:IllegalStateException)
}
