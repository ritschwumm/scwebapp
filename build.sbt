inThisBuild(Seq(
	organization	:= "de.djini",
	version			:= "0.228.0",

	scalaVersion	:= "2.13.1",
	scalacOptions	++= Seq(
		"-deprecation",
		"-unchecked",
		"-feature",
		"-Xfatal-warnings",
		"-Xlint"
	),

	conflictManager	:= ConflictManager.strict withOrganization "^(?!(org\\.scala-lang|org\\.scala-js)(\\..*)?)$",

	wartremoverErrors	++= Seq(
		Wart.AsInstanceOf,
		Wart.IsInstanceOf,
		Wart.StringPlusAny,
		Wart.ToString,
		Wart.EitherProjectionPartial,
		Wart.OptionPartial,
		Wart.TryPartial,
		Wart.Enumeration,
		Wart.FinalCaseClass,
		Wart.JavaConversions,
		Wart.Option2Iterable,
		Wart.JavaSerializable,
		//Wart.Any,
		Wart.AnyVal,
		//Wart.Nothing,
		Wart.ArrayEquals,
		Wart.ImplicitParameter,
		Wart.ExplicitImplicitTypes,
		Wart.LeakingSealed,
		//Wart.DefaultArguments,
		Wart.Overloading,
		//Wart.PublicInference,
		Wart.TraversableOps,
	)
))

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
	(project in file("modules/core"))
	.settings(
		scalacOptions	++= Seq(
			"-language:implicitConversions"
		),
		libraryDependencies	++= Seq(
			"de.djini"		%%	"scutil-jdk"	% "0.177.0"	% "compile",
			"de.djini"		%%	"scparse-ng"	% "0.183.0"	% "compile",
			"org.specs2"	%%	"specs2-core"	% "4.9.3"	% "test"
		)
	)

lazy val `scwebapp-servlet`	=
	(project in file("modules/servlet"))
	.settings(
		scalacOptions	++= Seq(
			"-language:implicitConversions"
		),
		libraryDependencies	++= Seq(
			"de.djini"		%%	"scutil-jdk"		% "0.177.0"	% "compile",
			"javax.servlet"	%	"javax.servlet-api"	% "3.1.0"	% "provided"
		)
	)
	.dependsOn(
		`scwebapp-core`
	)

lazy val `scwebapp-runner`	=
	(project in file("modules/runner"))
	.settings(
		libraryDependencies		++= Seq(
			"org.eclipse.jetty"	%	"jetty-server"	% "9.4.28.v20200408"	% "compile"
		)
	)
	.dependsOn(
		`scwebapp-servlet`
	)
