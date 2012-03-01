package scwebapp

import scutil.Functions._
import scutil.Implicits._

import HttpServletRequestImplicits._

object HttpCondition {
	// http
	val OptionsMethod:HttpCondition	= MethodIs("OPTIONS")
	val HeadMethod:HttpCondition	= MethodIs("HEAD")
	val GetMethod:HttpCondition		= MethodIs("GET")
	val PostMethod:HttpCondition	= MethodIs("POST")
	val PutMethod:HttpCondition		= MethodIs("PUT")
	val DeleteMethod:HttpCondition	= MethodIs("DELETE")
	val TraceMethod:HttpCondition	= MethodIs("TRACE")
	val ConnectMethod:HttpCondition	= MethodIs("CONNECT")
	
	// webdav
	val PropfindMethod:HttpCondition	= MethodIs("PROPFIND")
	val ProppatchMethod:HttpCondition	= MethodIs("PROPPATCH")
	val MkcolMethod:HttpCondition		= MethodIs("MKCOL")
	val CopyMethod:HttpCondition		= MethodIs("COPY")
	val MoveMethod:HttpCondition		= MethodIs("MOVE")
	val LockMethod:HttpCondition		= MethodIs("LOCK")
	val UnlockMethod:HttpCondition		= MethodIs("UNLOCK")
			
	//------------------------------------------------------------------------------
	
	def MethodIs(method:String):HttpCondition	=
			request => request.getMethod.toUpperCase ==== method.toUpperCase

	def MethodIn(methods:String*):HttpCondition	=
			methodIn(methods.toSet)
	
	private def methodIn(methods:Set[String]):HttpCondition	= 
			request	=> (methods map { _.toUpperCase }) contains request.getMethod.toUpperCase
          
	//------------------------------------------------------------------------------
			
	def AtFullPath(path:String):HttpCondition	=
			request	=> request.fullPath == path
			
	def AtServletPath(path:String):HttpCondition	=
			request	=> request.getServletPath == path
}
