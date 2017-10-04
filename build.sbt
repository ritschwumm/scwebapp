inThisBuild(Seq(
	organization	:= "de.djini",
	version			:= "0.154.0",
	
	scalaVersion	:= "2.12.3",
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

lazy val warts	=
		Seq(
			Wart.StringPlusAny,
			Wart.EitherProjectionPartial,
			Wart.OptionPartial,
			Wart.Enumeration,
			Wart.FinalCaseClass,
			Wart.JavaConversions,
			Wart.Option2Iterable,
			Wart.TryPartial
		)

//------------------------------------------------------------------------------

lazy val `scwebapp`	=
		(project in file("."))
		.aggregate(
			`scwebapp-core`,
			`scwebapp-servlet`,
			`scwebapp-runner`
		)
		.settings(
			publishArtifact := false
		)

lazy val `scwebapp-core`	=
		(project in file("sub/core"))
		.settings(
			scalacOptions	++= Seq(
				"-language:implicitConversions"
			),
			libraryDependencies	++= Seq(
				"de.djini"		%%	"scutil-core"	% "0.117.0"	% "compile",
				"org.specs2"	%%	"specs2-core"	% "3.9.5"	% "test"
			),
			wartremoverErrors ++= warts
		)
		
lazy val `scwebapp-servlet`	=
		(project in file("sub/servlet"))
		.settings(
			scalacOptions	++= Seq(
				"-language:implicitConversions"
			),
			libraryDependencies	++= Seq(
				"de.djini"		%%	"scutil-core"		% "0.117.0"	% "compile",
				"javax.servlet"	%	"javax.servlet-api"	% "3.1.0"	% "provided"
			),
			wartremoverErrors ++= warts
		)
		.dependsOn(
			`scwebapp-core`
		)
		
lazy val `scwebapp-runner`	=
		(project in file("sub/runner"))
		.settings(
			libraryDependencies		++= Seq(
				"org.eclipse.jetty"	%	"jetty-server"	% "9.4.6.v20170531"	% "compile"
			),
			wartremoverErrors ++= warts
		)
		.dependsOn(
			`scwebapp-servlet`
		)
