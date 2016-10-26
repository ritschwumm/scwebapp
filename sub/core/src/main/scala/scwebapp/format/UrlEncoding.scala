package scwebapp.format

import java.net.URLDecoder
import java.nio.charset.Charset
import java.io.UnsupportedEncodingException

import scutil.lang._
import scutil.implicits._
import scutil.codec._

import scwebapp.data._

object UrlEncoding {
	// TODO String is not a great problem type
	
	def parseQueryParameters(queryString:String, encoding:Charset):Tried[String,CaseParameters]	=
			decode(queryString, uriDecode(encoding))
			
	def parseForm(formData:String, encoding:Charset):Tried[String,CaseParameters]	=
			decode(formData, urlDecode(encoding))
		
	private def uriDecode(encoding:Charset)(queryString:String):Tried[String,String]	=
			URIComponent forCharset encoding decode queryString mapFail {
				case URIComponentInvalid(position)		=> so"invalid uri encoding at position ${position.toString}"
				case URIComponentException(underlying)	=> so"invalid character encoding: ${underlying.getMessage}"
			}
		
	private def urlDecode(encoding:Charset)(formData:String):Tried[String,String]	=
			try {
				Win(URLDecoder decode (formData, encoding.name))
			}
			catch { case e:UnsupportedEncodingException =>
				Fail(e.getMessage)
			}
		
	private def decode(data:String, decode:String=>Tried[String,String]):Tried[String,CaseParameters]	=
			if (data.isEmpty)	Win(CaseParameters.empty)
			else {
				val triedPairs:ISeq[Tried[String,(String,String)]]	=
						data
						.splitAroundChar ('&')
						.map { part =>
							part splitAroundFirstChar '=' match {
								case Some((key, value))	=> Tried zip2 (decode(key), decode(value))
								case None				=> decode(part) map (_ -> "")
							}
						}
						
				triedPairs.sequenceTried map CaseParameters.apply
			}
}
