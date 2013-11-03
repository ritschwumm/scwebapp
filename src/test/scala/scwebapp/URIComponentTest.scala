package scwebapp

import org.specs2.mutable._

import scutil.io.Charsets._

class URIComponentTest extends Specification {
	"URIComponent" should {
		"roundtrip all usual chars" in {
			val str	= 0 until 256 map { _.toChar } mkString ""
			val	enc	= URIComponent encode (str, utf_8)
			val	dec	= URIComponent decode (enc, utf_8)
			dec mustEqual str
		}   
		"encode plus as %2b" in {
			URIComponent encode ("+", utf_8) mustEqual "%2B"
		}
		"encode blank as %20" in {
			URIComponent encode (" ", utf_8) mustEqual "%20"
		}
		"decode plus as plus" in {
			URIComponent decode ("+", utf_8) mustEqual "+"
		}
		
		/*
		import javax.script.ScriptEngineManager
		
		"encode everything just like encodeURIComponent" in {
			val engine	= new ScriptEngineManager getEngineByName "JavaScript"
			val str	= 0 until 256 map { _.toChar } mkString ""
			val s1	= URIComponent encode (str, utf_8)
			engine put ("str", str)
			val s2	= engine eval """encodeURIComponent(str)"""
			s1 mustEqual s2
		}
		
		"decode everything just like encodeURIComponent" in {
			val engine	= new ScriptEngineManager getEngineByName "JavaScript"
			val str	= 0 until 256 map { _.toChar } mkString ""
			engine put ("str", str)
			val s1	= (engine eval """encodeURIComponent(str)""").asInstanceOf[String]
			val s2	= URIComponent decode (s1, utf_8)
			s2 mustEqual str
		}
		*/
	}
}
