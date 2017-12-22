package scwebapp.servlet.bootstrap

sealed trait Note
final case class NoteDefault(key:String, value:String)				extends Note
final case class NoteChange(key:String, value:String)				extends Note
final case class NoteError(key:String, value:String, error:String)	extends Note
