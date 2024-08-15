package scwebapp.handler

import scutil.core.implicits.*
import scutil.jdk.implicits.*
import scutil.lang.*
import scutil.log.*
import scutil.codec.*
import scutil.security.*

import scwebapp.*
import scwebapp.instances.*
import scwebapp.status.*
import scwebapp.source.*
import scwebapp.data.*
import scwebapp.util.MimeMapping

object StaticHandler {
	type Path	= Nes[String]
}

/**
basePath		could be	META-INF/resources
alias			could be	Map("" -> "index.html")
serverCached	could be	_ => true
clientCached	could be	it => if (it == Nes.one("sw.js")) SourceCaching.Disabled else SourceCaching.Expires(_ + HttpDuration.day)
*/
final class StaticHandler(
	basePath:String,
	alias:Map[String,String],
	serverCached:StaticHandler.Path=>Boolean,
	clientCached:StaticHandler.Path=>SourceCaching
)
extends Logging {
	import StaticHandler.Path

	private val lastModified	= HttpDate.now()
	private val cache			= new SimpleCache[Path,SourceData](loadSource)

	val plan:HttpPHandler	=
		request => {
			(
				for {
					raw			<-	request.fullPathUTF8.toOption	.toRight(Some(badRequest))
					unprefixed	<-	raw.cutPrefix("/")				.toRight (Some(badRequest))
					aliased		=	alias.get(unprefixed).getOrElse(unprefixed)
					path		<-	parsePath(aliased)				.toRight(Some(badRequest))
					source		<-	getSource(path)					.toRight(None)
				}
				yield SourceHandler.plan(source).apply(request)
			)
			.map(Some.apply)
			.merge
		}

	private def getSource(path:Path):Option[SourceData]	=
		if (serverCached(path))	cache.fetch(path)
		else					loadSource(path)

	// TODO lastModified should be configurable, maybe
	private def loadSource(path:Path):Option[SourceData]	=
		path.into(resourcePath).into(readResource).map { bytes =>
			val mimeType		= mimeTypeFor(path)
			SourceData(
				size			= bytes.size.toLong,
				range			= HttpOutput.writeByteStringRange(bytes, _),
				contentId		= hashString(bytes),
				lastModified	= lastModified,
				caching			= clientCached(path),
				mimeType		= mimeType,
				disposition		= None,
				enableGZIP		= doGzip(mimeType)
			)
		}

	private def hashString(it:ByteString):String =
		Hex.encodeByteString(
			Hashing.hash("SHA-256", 1, it)
		)

	private def resourcePath(path:Path):String	=
		(basePath +: path).toVector mkString "/"

	private def readResource(path:String):Option[ByteString]	=
		getClass.getClassLoader.classpathResource(path).map(_.byteString)

	private def parsePath(it:String):Option[Path]	=
		it.splitAroundChar('/').optionBy(_.forall(validPart)).flatMap(Nes.fromSeq)

	private def validPart(it:String):Boolean	=
		it != ""	&&
		it != ".."	&&
		re"[a-zA-Z0-9._-]+".test(it)

	private def mimeTypeFor(it:Path):MimeType	=
		MimeMapping.default.forFileName(it.last).map(fixCharset).getOrElse(application_octetStream)

	private def fixCharset(it:MimeType):MimeType	=
		doCharset(it).cata(
			it,
			it.addParameter("charset", "UTF-8")
		)

	private def doCharset(it:MimeType):Boolean	=
		(it.major, it.minor) match {
			case ("text",			"xml")			=> false
			case ("text",			_)				=> true
			case ("application",	"javascript")	=> true
			case _									=> false
		}

	private def doGzip(it:MimeType):Boolean	=
		(it.major, it.minor) match {
			case ("text",			_)				=> true
			case ("application",	"javascript")	=> true
			case ("application",	"json")			=> true
			case _									=> false
		}

	private val badRequest	= HttpResponder.sync(HttpResponse(BAD_REQUEST))
}
