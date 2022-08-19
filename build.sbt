Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(Seq(
	organization	:= "de.djini",
	version			:= "0.281.0",

	scalaVersion	:= "3.1.2",
	scalacOptions	++= Seq(
		"-feature",
		"-deprecation",
		"-unchecked",
		"-Wunused:all",
		"-Xfatal-warnings",
		"-Ykind-projector:underscores",
	),

	versionScheme	:= Some("early-semver"),
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
		//Wart.TraversableOps,
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
		libraryDependencies	++= Seq(
			"de.djini"		%%	"scutil-jdk"	% "0.223.0"	% "compile",
			"de.djini"		%%	"scparse-ng"	% "0.230.0"	% "compile",
			"io.monix"		%%	"minitest"		% "2.9.6"	% "test"
		),
		testFrameworks	+= new TestFramework("minitest.runner.Framework"),
	)

lazy val `scwebapp-servlet`	=
	(project in file("modules/servlet"))
	.settings(
		libraryDependencies	++= Seq(
			"de.djini"			%%	"scutil-jdk"			% "0.223.0"	% "compile",
			"jakarta.servlet"	%	"jakarta.servlet-api"	% "5.0.0"	% "provided"
		)
	)
	.dependsOn(
		`scwebapp-core`
	)

lazy val `scwebapp-runner`	=
	(project in file("modules/runner"))
	.settings(
		libraryDependencies		++= Seq(
			"org.eclipse.jetty"	%	"jetty-server"	% "11.0.9"	% "compile"
		)
	)
	.dependsOn(
		`scwebapp-servlet`
	)
