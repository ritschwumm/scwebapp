Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(Seq(
	organization	:= "de.djini",
	version			:= "0.312.0",

	scalaVersion	:= "3.5.0",
	scalacOptions	++= Seq(
		"-feature",
		"-deprecation",
		"-unchecked",
		"-source:future",
		"-Wunused:all",
		"-Xfatal-warnings",
		"-Xkind-projector:underscores",
	),

	versionScheme	:= Some("early-semver"),

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
		//Wart.TraversableOps,
	)
))

//------------------------------------------------------------------------------

lazy val `scwebapp`	=
	project.in(file("."))
	.aggregate(
		`scwebapp-core`,
		`scwebapp-servlet`,
		`scwebapp-runner`
	)
	.settings(
		publishArtifact := false
	)

lazy val `scwebapp-core`	=
	project.in(file("modules/core"))
	.settings(
		libraryDependencies	++= Seq(
			"de.djini"		%%	"scutil-jdk"	% "0.250.0"	% "compile",
			"de.djini"		%%	"scparse-ng"	% "0.259.0"	% "compile",
			"io.monix"		%%	"minitest"		% "2.9.6"	% "test"
		),
		testFrameworks	+= new TestFramework("minitest.runner.Framework"),
	)

lazy val `scwebapp-servlet`	=
	project.in(file("modules/servlet"))
	.settings(
		libraryDependencies	++= Seq(
			"de.djini"			%%	"scutil-jdk"			% "0.250.0"	% "compile",
			"jakarta.servlet"	%	"jakarta.servlet-api"	% "5.0.0"	% "provided"
		)
	)
	.dependsOn(
		`scwebapp-core`
	)

lazy val `scwebapp-runner`	=
	project.in(file("modules/runner"))
	.settings(
		libraryDependencies		++= Seq(
			"org.eclipse.jetty"	%	"jetty-server"			% "11.0.22"	% "compile",
			"jakarta.servlet"	%	"jakarta.servlet-api"	% "5.0.0"	% "provided"
		)
	)
	.dependsOn(
		`scwebapp-servlet`
	)
