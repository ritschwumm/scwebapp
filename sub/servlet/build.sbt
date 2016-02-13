name			:= "scwebapp-servlet"

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
	"de.djini"		%%	"scutil-core"		% "0.77.0"	% "compile",
	"javax.servlet"	%	"javax.servlet-api"	% "3.1.0"	% "provided"
)
