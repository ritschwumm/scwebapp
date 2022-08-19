package scwebapp.servlet.bootstrap

enum Note {
	case Default(key:String, value:String)
	case Change(key:String, value:String)
	case Error(key:String, value:String, error:String)
}
