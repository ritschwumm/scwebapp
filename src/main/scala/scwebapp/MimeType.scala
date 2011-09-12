package scwebapp

// @see http://tools.ietf.org/html/rfc2046
// @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1

/*
top-level:	text image audio video application multipart message
*/

object MimeType {
	def apply(major:String, minor:String):MimeType	= MimeType(major, minor, Map.empty)
	
	private val	 RE1	= """^([a-z]+|\*)/([a-z*]+|\*)(.*)$""".r
	def parse(s:String):Option[MimeType]	= {
		s match {
			case RE1(major, minor, rest)	=>
				parseParams(rest) match {
					case Some(params)	=> Some(MimeType(major, minor, params))
					case None			=> None
				}
			case _							=> None
		}
	}
	
	private val RE2	= """;\s*([^;= ]+)\s*=\s*([^;= ]*)\s*""".r
	def parseParams(s:String):Option[Map[String,String]]	= {
		var	out	= Map.empty[String,String]
		var	ss	= s.trim:CharSequence
		while (true) {
			RE2 findPrefixMatchOf ss match {
				case Some(matched)	=> 
					out	+= ((matched group 1, matched group 2))
					if (matched.after != null)	ss	= matched.after 
					else						ss	= ""
				case None	=> 
					if (ss.length == 0)	return Some(out)
					else				return None
			}
		}
		sys error "silence, i kill you"
	}
}
	
case class MimeType(major:String, minor:String, parameters:Map[String,String]) {
	def value:String = major + "/" + minor + (parameters map { case (key,value) => "; " + key + "=" + value } mkString "")
}
