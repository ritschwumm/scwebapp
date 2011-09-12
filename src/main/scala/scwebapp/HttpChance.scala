package scwebapp

import scutil.Functions._

import HttpChanceImplicits._
import HttpRouteImplicits._

object HttpChance {
	val Reject:HttpChance	= constant(None)
	
	def Alternate(chances:HttpChance*):HttpChance	= 
			alternate(chances)
			
	private def alternate(chances:Seq[HttpChance])	=
			chances.foldLeft(Reject)(_ orElse _)
	
	def Route(routes:HttpRoute*):HttpChance	=
			route(routes)
			
	private def route(routes:Seq[HttpRoute]):HttpChance	=
			alternate(routes map { _.toChance })
}
