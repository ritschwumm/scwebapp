package scwebapp.parser

import scala.annotation.tailrec

import scutil.lang._

object Parser {
	import Result.{Success,Failure}

	def success[S,T](t:T):Parser[S,T]	=
			Parser { i:Input[S] => Result.Success(i, t) }

	def failure[S]:Parser[S,Unit]	=
			Parser { i => Result.Failure(i) }

	def any[S]:Parser[S,S]	=
			Parser { i =>
				i.next match {
					case Some((rest, item))	=> Success(rest, item)
					case None				=> Failure(i)
				}
			}

	def sat[S](pred:Predicate[S]):Parser[S,S]	=
			any[S] filter pred

	def is[S](c:S):Parser[S,S]	=
			sat(_ == c)

	def iss[S](cs:ISeq[S]):Parser[S,ISeq[S]]	=
			Parser { i =>
				@tailrec
				def loop(ii:Input[S], look:ISeq[S]):Result[S,ISeq[S]]	=
						if (look.isEmpty)	Success(ii, cs)
						else ii.next match {
							case Some((rest, item)) if item == look.head	=> loop(rest, look.tail)
							case _											=> Failure(ii)
						}
				loop(i, cs)
			}

	def end[S]:Parser[S,Unit]	=
			any[S].prevent
}

final case class Parser[S,+T](parse:Input[S]=>Result[S,T]) { self =>
	import Result.{Success,Failure}

	def filter(pred:Predicate[T]):Parser[S,T]	=
			Parser { i => self parse i filter pred }

	def withFilter(pred:Predicate[T])	= new GenWithFilter[T](self, pred)
	class GenWithFilter[+A](self:Parser[S,A], pred:Predicate[A]) {
		def map[B](func:A=>B):Parser[S,B]						= self filter pred map		func
		def flatMap[B](func:A=>Parser[S,B]):Parser[S,B]			= self filter pred flatMap	func
		def withFilter(further:Predicate[A]):GenWithFilter[A]	= new GenWithFilter[A](self, x => pred(x) && further(x))
	}

	def orElse[U>:T](that:Parser[S,U]):Parser[S,U]	=
			Parser { i => (self parse i) orElse (that parse i) }

	def map[U](func:T=>U):Parser[S,U]	=
			Parser { i => self parse i map func }

	def flatMap[U](func:T=>Parser[S,U]):Parser[S,U]	=
			Parser { i =>
				self parse i match {
					case Success(i1, t)	=> func(t) parse i1
					case Failure(i)		=> Failure(i)
				}
			}

	def flatten[U](implicit ev:T=>Parser[S,U]):Parser[S,U]	=
			flatMap(ev)

	def filterMap[U](pfunc:PFunction[T,U]):Parser[S,U]	=
			Parser { i => self parse i filterMap pfunc }

	def filterSome[U](implicit ev:PFunction[T,U]):Parser[S,U]	=
			filterMap(ev)

	def collect[U](pfunc:PartialFunction[T,U]):Parser[S,U]	=
			filterMap(pfunc.lift)

	// function effect first
	def ap[U,V](that:Parser[S,U])(implicit ev:T=>(U=>V)):Parser[S,V]	=
			for { a	<- self; b	<- that } yield a(b)

	def next[U](that:Parser[S,U]):Parser[S,(T,U)]	=
			for { a	<- self; b	<- that } yield (a, b)

	def tag[U](it:U):Parser[S,U]	=
			self map constant(it)

	def left(that:Parser[S,Any]):Parser[S,T]	=
			self next that map { _._1 }

	def right[U](that:Parser[S,U]):Parser[S,U]	=
			self next that map { _._2 }

	def inside(quote:Parser[S,Any]):Parser[S,T]	=
			quote right self left quote

	def flag:Parser[S,Boolean]	=
			Parser { i =>
				self parse i match {
					case Success(i1, _)	=> Success(i1,	true)
					case Failure(_)		=> Success(i,	false)
				}
			}

	def either[U](that:Parser[S,U]):Parser[S,Either[T,U]]	=
			(self map Left.apply)	orElse
			(that map Right.apply)

	def option:Parser[S,Option[T]]	=
			Parser { i =>
				self parse i match {
					case Success(i1, t)	=> Success(i1,	Some(t))
					case Failure(_)		=> Success(i,	None)
				}
			}

	def seq:Parser[S,ISeq[T]]	=
			Parser { i =>
				@tailrec
				def loop(ii:Input[S], accu:ISeq[T]):Result[S,ISeq[T]]	=
						self parse ii match {
							case Success(i1, t)	=> loop(i1, accu :+ t)
							case Failure(_)		=> Success(ii, accu)
						}
				loop(i, Vector.empty[T])
			}

	def nes:Parser[S,Nes[T]]	=
			self next self.seq map { case (x, xs) => Nes(x, xs) }

	def minmax(min:Int, max:Int):Parser[S,ISeq[T]]	=
			self upto max filter { _.size >= min }

	def times(count:Int):Parser[S,ISeq[T]]	=
			self upto count filter { _.size == count }

	def upto(count:Int):Parser[S,ISeq[T]]	=
			Parser { i =>
				@tailrec def loop(in:Input[S], accu:ISeq[T]):Result[S,ISeq[T]]	=
						if (accu.size == count)	Success(in, accu)
						else {
							self parse in match {
								case Success(nextIn, value)	=> loop(nextIn, accu :+ value)
								case Failure(_)				=> Success(in, accu)
							}
						}
				loop(i, Vector.empty[T])
			}

	def sepSeq(sepa:Parser[S,Any]):Parser[S,ISeq[T]]	=
			sepNes(sepa) map { _.toVector } orElse (Parser success Vector.empty)

	def sepNes(sepa:Parser[S,Any]):Parser[S,Nes[T]]	=
			self next (sepa right self).seq map { case (x, xs) => Nes(x, xs) }

	def guard:Parser[S,Unit]	=
			prevent.prevent

	def prevent:Parser[S,Unit]	=
			Parser { i =>
				self parse i match {
					case Success(_, _)	=> Failure(i)
					case Failure(_)		=> Success(i, ())
				}
			}

	def eating(ws:Parser[S,Any]):Parser[S,T]	=
			ws.option right self

	def finish(ws:Parser[S,Any]):Parser[S,T]	=
			self left ws.option left Parser.end

	def phrase:Parser[S,T]	=
			self left Parser.end

	def nest[U,V](mkInput:T=>Input[U], inner:Parser[U,V]):Parser[S,V]	=
			Parser { selfInput =>
				self parse selfInput match {
					case Success(selfRemainder, selfValue)	=>
						inner.phrase parse mkInput(selfValue) match {
							case Success(innerRemainder, innerValue)	=> Success(selfRemainder, innerValue)
							case Failure(innerRemainder)				=> Failure(selfInput)
						}
					case Failure(selfRemainder)	=> Failure(selfRemainder)
				}
			}
}
