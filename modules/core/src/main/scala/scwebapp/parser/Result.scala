package scwebapp.parser

import scutil.lang._

sealed trait Result[+S,+T] {
	def map[U](func:T=>U):Result[S,U]	=
			this match {
				case Success(i, t)	=> Success(i, func(t))
				case Failure(i)		=> Failure(i)
			}
			
	def filter(pred:Predicate[T]):Result[S,T]	=
			this match {
				case Success(i, t)	if pred(t)	=> Success(i, t)
				case Success(i, _)				=> Failure(i)
				case Failure(i)					=> Failure(i)
			}
			
	def orElse[SS>:S,TT>:T](that:Result[SS,TT]):Result[SS,TT]	=
			(this, that) match {
				// TODO Failure(i1 max i2)
				case (Failure(i1),		Failure(i2))	=> Failure(i1)
				case (Success(i, t),	Failure(_))		=> Success(i, t)
				case (Failure(_),		Success(i, t))	=> Success(i, t)
				case (Success(i, t),	Success(_, _))	=> Success(i, t)
			}
			
	def filterMap[U](pfunc:PFunction[T,U]):Result[S,U]	=
			this map pfunc match {
				case Success(i, Some(u))	=> Success(i, u)
				case Success(i, None)		=> Failure(i)
				case Failure(i)				=> Failure(i)
			}
			
	def collect[U](pfunc:PartialFunction[T,U]):Result[S,U]	=
			filterMap(pfunc.lift)
		
	def toOption:Option[T]	=
			this match {
				case Success(i, t)	=> Some(t)
				case Failure(i)		=> None
			}
}

final case class Success[S,T](i:Input[S], t:T)	extends Result[S,T]
final case class Failure[S](i:Input[S])			extends Result[S,Nothing]
