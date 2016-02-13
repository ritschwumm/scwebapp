package scwebapp

import java.nio.charset.Charset

import scutil.lang._
import scutil.implicits._

import scwebapp.header._
import scwebapp.data._

final case class HttpHeaders(params:NoCaseParameters) {
	/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
	def first[T](typ:HeaderType[T]):Tried[String,Option[T]]	=
			(params firstString typ.key map typ.parseTried).sequenceTried
			
	/** Fail is invalid, Win(None) if missing, Win(Some) if valid */
	def all[T](typ:HeaderType[T]):Tried[String,ISeq[T]]	=
			(params get typ.key map typ.parseTried).sequenceTried
		
	//------------------------------------------------------------------------------
	
	def encoding:Tried[String,Option[Charset]]	=
			first(ContentType)
			.map {
				_ map {
					_.typ.charset
				}
			}
			match {
				case Fail(x)					=> Fail(x)
				case Win(None)					=> Win(None)
				case Win(Some(Fail(x)))			=> Fail(x)
				case Win(Some(Win(None)))		=> Win(None)
				case Win(Some(Win(Some(x))))	=> Win(Some(x))
			}
			
	// TODO for HttpPart, this is wrong per rfc:
	// Parameters is for regular headers, not for multipart parts
	def fileName(headers:Parameters):Tried[String,Option[String]]	=
			first(ContentDisposition)
			.map {
				_ map {
					_.fileName
				}
			}
			match {
				case Fail(x)	=> Fail(x)
				case Win(x)		=> Win(x.flatten)
			}
		
}
