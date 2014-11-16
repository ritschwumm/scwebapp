package scwebapp
package pimp

import java.util.{ Enumeration=>JEnumeration }

// TODO scutil 0.58.0
object EnumerationUtil {
	def toIterator[T](it:JEnumeration[T]):Iterator[T]	=
			new Iterator[T] {
				def hasNext	= it.hasMoreElements
				def next	= it.nextElement
			}
}
