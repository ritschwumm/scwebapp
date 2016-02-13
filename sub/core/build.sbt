name			:= "scwebapp-core"

scalacOptions	++= Seq(
	"-language:implicitConversions"//,
	// "-language:existentials",
	// "-language:higherKinds",
	// "-language:reflectiveCalls",
	// "-language:dynamics",
	// "-language:postfixOps",
	// "-language:experimental.macros"
)

libraryDependencies	++= Seq(
	"de.djini"		%%	"scutil-core"	% "0.77.0"	% "compile",
	"org.specs2"	%%	"specs2-core"	% "3.7"	% "test"
)
