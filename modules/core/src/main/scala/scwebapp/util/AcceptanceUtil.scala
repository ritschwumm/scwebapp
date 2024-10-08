package scwebapp.util

import scutil.core.implicits.*

import scwebapp.data.*

object AcceptanceUtil {
	@SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
	def acceptance[T](matches:Seq[T])(extract:T=>Option[(Int,QValue)]):Option[QValue]	=
		matches
		.mapFilter(extract)
		// get the highest rank
		.groupBy	{ (level, _)	=> level	}
		.toVector
		.sortBy		{ (level, _)	=> level	}
		.lastOption
		.map(
			// find maximum QValue in the highest rank
			_._2.map(_._2).max
		)
}
