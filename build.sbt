name			:= "scwebapp"

organization	:= "de.djini"

version			:= "0.14.0"

scalaVersion	:= "2.10.0"

libraryDependencies	++= Seq(
	"de.djini"		%%	"scutil"			% "0.17.0"	% "compile",
	"javax.servlet"	%	"javax.servlet-api"	% "3.0.1"	% "provided"
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
