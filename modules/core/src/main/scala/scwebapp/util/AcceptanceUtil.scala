package scwebapp.util

import scutil.base.implicits._
import scutil.lang._

import scwebapp.data._

object AcceptanceUtil {
	@SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
	def acceptance[T](matches:ISeq[T])(extract:T=>Option[(Int,QValue)]):Option[QValue]	=
			matches
			.collapseMap	(extract)
			.groupMap		(identity)
			.toVector
			// highest rank first
			.sortBy			{ case (level, _)	=> -level	}
			.map			{ case (_, group)	=> group	}
			.filter			{ _.nonEmpty	}
			// highest rank only
			.headOption
			// NOTE this is safe, we filtered for nonEmpty before
			.map			{ _.max			}
}
