name			:= "scwebapp"

organization	:= "de.djini"

version			:= "0.30.0"

scalaVersion	:= "2.10.3"

libraryDependencies	++= Seq(
	"de.djini"		%%	"scutil"			% "0.31.0"	% "compile",
	"javax.servlet"	%	"javax.servlet-api"	% "3.0.1"	% "provided",
	"org.specs2"	%%	"specs2"			% "2.2.3"	% "test"	exclude("org.scala-lang", "scala-library")
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
