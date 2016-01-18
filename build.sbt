name			:= "scwebapp"
organization	:= "de.djini"
version			:= "0.92.0"

scalaVersion	:= "2.11.7"
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
	"de.djini"		%%	"scutil-core"		% "0.74.0"	% "compile",
	"javax.servlet"	%	"javax.servlet-api"	% "3.1.0"	% "provided",
	"org.specs2"	%%	"specs2-core"		% "3.6.6"	% "test"
)
dependencyOverrides	++= Set(
	"org.scala-lang"	% "scala-library"	% scalaVersion.value,
	"org.scala-lang"	% "scala-reflect"	% scalaVersion.value
)
