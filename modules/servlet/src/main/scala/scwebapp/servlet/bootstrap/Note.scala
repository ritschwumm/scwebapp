package scwebapp.servlet.bootstrap

object Note {
	final case class Default(key:String, value:String)				extends Note
	final case class Change(key:String, value:String)				extends Note
	final case class Error(key:String, value:String, error:String)	extends Note
}

sealed trait Note
