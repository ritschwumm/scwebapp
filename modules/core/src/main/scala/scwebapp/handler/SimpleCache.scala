package scwebapp.handler

import scutil.core.implicits.*
import scutil.lang.*

final class SimpleCache[K,V](load:K=>Option[V]) {
	private val cache:Synchronized[Map[K,V]]	= Synchronized(Map.empty)

	def fetch(key:K):Option[V]	=
		cache.modify { map	=>
			map.get(key)
			.cata (
				load(key).secondBy { valueOpt =>
					valueOpt.cata(
						map,
						value => map + (key -> value)
					)
				},
				value => (map -> Some(value))
			)
		}
}
