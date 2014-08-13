name			:= "scwebapp"

organization	:= "de.djini"

version			:= "0.53.0"

scalaVersion	:= "2.11.1"

libraryDependencies	++= Seq(
	"de.djini"		%%	"scutil-core"		% "0.46.0"	% "compile",
	"javax.servlet"	%	"javax.servlet-api"	% "3.0.1"	% "provided",
	"org.specs2"	%%	"specs2"			% "2.3.13"	% "test"	exclude("org.scala-lang", "scala-library")	exclude("org.scala-lang", "scala-reflect")
)

scalacOptions	++= Seq(
	"-deprecation",
	"-unchecked",
	"-language:implicitConversions",
	// "-language:existentials",
	// "-language:higherKinds",
	// "-language:reflectiveCalls",
	// "-language:dynamics",
	"-language:postfixOps",
	// "-language:experimental.macros"
	"-feature"
)
