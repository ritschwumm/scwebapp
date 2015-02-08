name			:= "scwebapp"
organization	:= "de.djini"
version			:= "0.71.0"

scalaVersion	:= "2.11.5"
scalacOptions	++= Seq(
	"-deprecation",
	"-unchecked",
	"-language:implicitConversions",
	// "-language:existentials",
	// "-language:higherKinds",
	// "-language:reflectiveCalls",
	// "-language:dynamics",
	// "-language:postfixOps",
	// "-language:experimental.macros"
	"-feature",
	"-Ywarn-unused-import",
	"-Xfatal-warnings"
)

conflictManager	:= ConflictManager.strict
resolvers		+= "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
libraryDependencies	++= Seq(
	"de.djini"		%%	"scutil-core"		% "0.64.0"	% "compile",
	"javax.servlet"	%	"javax.servlet-api"	% "3.0.1"	% "provided",
	"org.specs2"	%%	"specs2"			% "2.4.16"	% "test"
)
dependencyOverrides	++= Set(
	"org.scala-lang"	% "scala-library"	% scalaVersion.value,
	"org.scala-lang"	% "scala-reflect"	% scalaVersion.value
)
