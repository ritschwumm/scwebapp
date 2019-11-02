inThisBuild(Seq(
	organization	:= "de.djini",
	version			:= "0.199.0",

	scalaVersion	:= "2.12.8",
	scalacOptions	++= Seq(
		"-deprecation",
		"-unchecked",
		"-feature",
		"-Ywarn-unused-import",
		"-Xfatal-warnings",
		"-Xlint"
	),

	conflictManager	:= ConflictManager.strict,
	resolvers		+= "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",

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
				"de.djini"		%%	"scutil-core"	% "0.157.0"	% "compile",
				"org.specs2"	%%	"specs2-core"	% "4.5.1"	% "test"
			)
		)

lazy val `scwebapp-servlet`	=
		(project in file("modules/servlet"))
		.settings(
			scalacOptions	++= Seq(
				"-language:implicitConversions"
			),
			libraryDependencies	++= Seq(
				"de.djini"		%%	"scutil-core"		% "0.157.0"	% "compile",
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
				"org.eclipse.jetty"	%	"jetty-server"	% "9.4.18.v20190429"	% "compile"
			)
		)
		.dependsOn(
			`scwebapp-servlet`
		)
