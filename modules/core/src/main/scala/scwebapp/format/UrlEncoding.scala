package scwebapp.format

import java.net.URLDecoder
import java.nio.charset.Charset
import java.io.UnsupportedEncodingException

import scutil.base.implicits._
import scutil.codec._

import scwebapp.data._

object UrlEncoding {
	// TODO String is not a great problem type

	def parseQueryParameters(queryString:String, encoding:Charset):Either[String,CaseParameters]	=
		decode(queryString, uriDecode(encoding))

	def parseForm(formData:String, encoding:Charset):Either[String,CaseParameters]	=
		decode(formData, urlDecode(encoding))

	private def uriDecode(encoding:Charset)(queryString:String):Either[String,String]	=
		URIComponent forCharset encoding decode queryString leftMap {
			case URIComponentInvalid(position)		=> show"invalid uri encoding at position ${position}"
			case URIComponentException(underlying)	=> show"invalid character encoding: ${underlying.getMessage}"
		}

	private def urlDecode(encoding:Charset)(formData:String):Either[String,String]	=
		try {
			Right(URLDecoder decode (formData, encoding.name))
		}
		catch { case e:UnsupportedEncodingException =>
			Left(e.getMessage)
		}

	private def decode(data:String, decode:String=>Either[String,String]):Either[String,CaseParameters]	=
		if (data.isEmpty)	Right(CaseParameters.empty)
		else {
			val triedPairs:Seq[Either[String,(String,String)]]	=
					data
					.splitAroundChar ('&')
					.map { part =>
						part splitAroundFirstChar '=' match {
							case Some((key, value))	=> decode(key) zip decode(value)
							case None				=> decode(part) map (_ -> "")
						}
					}

			triedPairs.sequenceEither map CaseParameters.apply
		}
}
