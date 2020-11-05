inThisBuild(Seq(
	organization	:= "de.djini",
	version			:= "0.236.0",

	scalaVersion	:= "2.13.3",
	scalacOptions	++= Seq(
		"-feature",
		"-deprecation",
		"-unchecked",
		"-Werror",
		"-Xlint",
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
			"de.djini"		%%	"scutil-jdk"	% "0.185.0"	% "compile",
			"de.djini"		%%	"scparse-ng"	% "0.191.0"	% "compile",
			"org.specs2"	%%	"specs2-core"	% "4.10.5"	% "test"
		)
	)

lazy val `scwebapp-servlet`	=
	(project in file("modules/servlet"))
	.settings(
		scalacOptions	++= Seq(
			"-language:implicitConversions"
		),
		libraryDependencies	++= Seq(
			"de.djini"		%%	"scutil-jdk"		% "0.185.0"	% "compile",
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
			"org.eclipse.jetty"	%	"jetty-server"	% "9.4.32.v20200930"	% "compile"
		)
	)
	.dependsOn(
		`scwebapp-servlet`
	)
