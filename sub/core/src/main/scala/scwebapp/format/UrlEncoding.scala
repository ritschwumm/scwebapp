package scwebapp.format

import java.net.URLDecoder
import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._
import scutil.io.URIComponent

import scwebapp.data._

object UrlEncoding {
	def parseQueryParameters(queryString:String, encoding:Charset):CaseParameters	=
			decode(queryString, URIComponent forCharset encoding decode _)
			
	def parseForm(formData:String, encoding:Charset):CaseParameters	=
			decode(formData, URLDecoder decode (_, encoding.name))
		
	private def decode(data:String, decode:Endo[String]):CaseParameters	=
			if (data.isEmpty)	CaseParameters.empty
			else {
				val pairs	=
						data
						.splitAroundChar ('&')
						.map { part =>
							part splitAroundFirstChar '=' match {
								case Some((key, value))	=> (decode(key), decode(value))
								case None				=> (decode(part), "")
							}
						}
				CaseParameters(pairs)
			}
}
