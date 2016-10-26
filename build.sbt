inThisBuild(Seq(
	organization	:= "de.djini",
	version			:= "0.123.0",
	
	scalaVersion	:= "2.11.8",
	scalacOptions	++= Seq(
		"-deprecation",
		"-unchecked",
		"-feature",
		"-Ywarn-unused-import",
		"-Xfatal-warnings",
		"-Xlint"
	),
	
	conflictManager	:= ConflictManager.strict,
	resolvers		+= "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
))

//------------------------------------------------------------------------------

lazy val `scwebapp`	=
		project
		.in			(file("."))
		.aggregate	(`scwebapp-core`, `scwebapp-servlet`)
		.settings	(publishArtifact := false)

lazy val `scwebapp-core`	=
		project
		.in			(file("sub/core"))
		
lazy val `scwebapp-servlet`	=
		project
		.in			(file("sub/servlet"))
		.dependsOn	(`scwebapp-core`)
		
