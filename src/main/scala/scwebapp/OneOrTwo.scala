package scwebapp

object OneOrTwo {
	def one[A,B](a:A):OneOrTwo[A,B]			= One(a)
	def two[A,B](b:B):OneOrTwo[A,B]			= Two(b)
	def both[A,B](a:A, b:B):OneOrTwo[A,B]	= OneAndTwo(a, b)
}

sealed trait OneOrTwo[+A,+B]
final case class One[A](a:A) 				extends OneOrTwo[A,Nothing]
final case class Two[B](b:B) 				extends OneOrTwo[Nothing,B]
final case class OneAndTwo[A,B](a:A, b:B)	extends OneOrTwo[A,B]
