package scwebapp.parser

import scala.annotation.tailrec

import scutil.lang._

object Parser {
	def succeed[S]:Parser[S,Unit]	=
			Parser { i:Input[S] => Success(i, ()) }
		
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
			
	def accept[S](c:S):Parser[S,S]	=
			sat(_ == c)
		
	def end[S]:Parser[S,Unit]	=
			any[S].prevent
		
	def mkSymbol[S,T](ws:Parser[S,Any], sub:Parser[S,T]):Parser[S,T]	=
			ws.optional right sub
		
	def mkFinish[S,T](ws:Parser[S,Any], sub:Parser[S,T]):Parser[S,T]	=
			(sub left ws.optional).phrase
}
	
case class Parser[S,+T](parse:Input[S]=>Result[S,T]) { self =>
	def filter(pred:Predicate[T]):Parser[S,T]	=
			Parser { i => self parse i filter pred }
		
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
		
	def follow[U](that:Parser[S,U]):Parser[S,(T,U)]	=
			for {
				a	<- self
				b	<- that
			}
			yield (a, b)
			
	def optional:Parser[S,Option[T]]	=
			Parser { i => 
				self parse i match { 
					case Success(i1, t)	=> Success(i1,	Some(t))
					case Failure(_)		=> Success(i,	None)
				} 
			}
	
	def many:Parser[S,Seq[T]]	=
			Parser { i =>
				@tailrec
				def loop(ii:Input[S], accu:Seq[T]):Result[S,Seq[T]]	=
						self parse ii match {
							case Success(i1, t)	=> loop(i1, accu :+ t)
							case Failure(_)		=> Success(ii, accu)
						}
				loop(i, Vector.empty[T])
			}
			
	def many1:Parser[S,Nes[T]]	=
			self follow self.many map { case (x, xs) => Nes(x, xs) }
		
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
			
	def left(that:Parser[S,Any]):Parser[S,T]	=
			self follow that map { case (t, _) => t }
		
	def right[U](that:Parser[S,U]):Parser[S,U]	=
			self follow that map { case (_, u) => u }
		
	def phrase:Parser[S,T]	=
			self left Parser.end
}
