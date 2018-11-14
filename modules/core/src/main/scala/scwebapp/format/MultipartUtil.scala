package scwebapp.format

import java.util.Random

import scutil.base.implicits._

object MultipartUtil {
	private val multipartChars	= "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

	private val random	= new Random

	def multipartBoundary():String	=
			random string (
				multipartChars,
				30 + (random nextInt 10)
			)
}
