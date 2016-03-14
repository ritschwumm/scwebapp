package scwebapp.source

final case class SourceDisposition(
	attachment:Boolean,
	fileName:Option[String]
)
