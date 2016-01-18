package scwebapp
package format

import java.util.Locale

object CaseUtil {
	def lowerCase(s:String):String	= s toLowerCase Locale.ENGLISH
}
