package scwebapp.parser

import scala.annotation.tailrec

import scutil.lang._

object Parser {
	def succeed[S,T](t:T):Parser[S,T]	=
			Parser { i:Input[S] => Success(i, t) }
		
	def fail[S]:Parser[S,Unit]	=
			Parser { i => Failure(i) }
	
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
		
	def end[S]:Parser[S,Unit]	=
			any[S].prevent
}
	
case class Parser[S,+T](parse:Input[S]=>Result[S,T]) { self =>
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
		
	def collect[U](pfunc:PartialFunction[T,U]):Parser[S,U]	=
			filterMap(pfunc.lift)
		
	def next[U](that:Parser[S,U]):Parser[S,(T,U)]	=
			for {
				a	<- self
				b	<- that
			}
			yield (a, b)
			
	def tag[U](it:U):Parser[S,U]	=
			self map constant(it)
			
	def left(that:Parser[S,Any]):Parser[S,T]	=
			self next that map { _._1 }
		
	def right[U](that:Parser[S,U]):Parser[S,U]	=
			self next that map { _._2 }
			
	def option:Parser[S,Option[T]]	=
			Parser { i => 
				self parse i match { 
					case Success(i1, t)	=> Success(i1,	Some(t))
					case Failure(_)		=> Success(i,	None)
				} 
			}
			
	def either[U](that:Parser[S,U]):Parser[S,Either[T,U]]	=
			(self map Left.apply)	orElse
			(that map Right.apply)
	
	def seq:Parser[S,Seq[T]]	=
			Parser { i =>
				@tailrec
				def loop(ii:Input[S], accu:Seq[T]):Result[S,Seq[T]]	=
						self parse ii match {
							case Success(i1, t)	=> loop(i1, accu :+ t)
							case Failure(_)		=> Success(ii, accu)
						}
				loop(i, Vector.empty[T])
			}
			
	def nes:Parser[S,Nes[T]]	=
			self next self.seq map { case (x, xs) => Nes(x, xs) }
		
	def sepSeq(sepa:Parser[S,Any]):Parser[S,Seq[T]]	=
			sepNes(sepa) map { _.toVector } orElse (Parser succeed Vector.empty)
		
	def sepNes(sepa:Parser[S,Any]):Parser[S,Nes[T]]	=
			self next (sepa right self).seq map { case (x, xs) => Nes(x, xs) }
		
	def guard:Parser[S,Unit]	=
			Parser { i =>
				self parse i match {
					case Success(_, _)	=> Success(i, ())
					case Failure(_)		=> Failure(i)
				}
			}
			
	def prevent:Parser[S,Unit]	=
			Parser { i =>
				self parse i match {
					case Success(_, _)	=> Failure(i)
					case Failure(_)		=> Success(i, ())
				}
			}
		
	def token(ws:Parser[S,Any]):Parser[S,T]	=
			ws.option right self
		
	def finish(ws:Parser[S,Any]):Parser[S,T]	=
			self left ws.option left Parser.end
		
	def phrase:Parser[S,T]	=
			self left Parser.end
}
