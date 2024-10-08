package scwebapp

import java.nio.charset.Charset

import scutil.core.implicits.*

import scwebapp.header.*
import scwebapp.data.*

final case class HttpHeaders(params:NoCaseParameters) {
	/** Left if invalid, Right(None) if missing, Right(Some) if valid */
	def first[T](typ:HeaderType[T]):Either[String,Option[T]]	=
		params.firstString(typ.key).map(typ.parseEither).sequenceEither

	/** Left if invalid, Right(None) if missing, Right(Some) if valid */
	def all[T](typ:HeaderType[T]):Either[String,Seq[T]]	=
		params.get(typ.key).map(typ.parseEither).sequenceEither

	//------------------------------------------------------------------------------

	def encoding:Either[String,Option[Charset]]	=
		first(ContentType)
		.map(
			_.map(_.typ.charset)
		)
		match {
			case Left(x)						=> Left(x)
			case Right(None)					=> Right(None)
			case Right(Some(Left(x)))			=> Left(x)
			case Right(Some(Right(None)))		=> Right(None)
			case Right(Some(Right(Some(x))))	=> Right(Some(x))
		}

	// TODO for HttpPart, this is wrong per RFC:
	// Parameters is for regular headers, not for multipart parts
	def fileName:Either[String,Option[String]]	=
		first(ContentDisposition)
		.map(
			_.map(_.fileName)
		)
		match {
			case Left(x)	=> Left(x)
			case Right(x)	=> Right(x.flatten)
		}
}
