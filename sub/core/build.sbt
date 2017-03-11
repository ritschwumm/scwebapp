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
	"de.djini"		%%	"scutil-core"	% "0.95.0"	% "compile",
	"org.specs2"	%%	"specs2-core"	% "3.8.7"	% "test"
)

wartremoverErrors ++= Seq(
	Wart.StringPlusAny,
	Wart.EitherProjectionPartial,
	Wart.OptionPartial,
	Wart.Enumeration,
	Wart.FinalCaseClass,
	Wart.JavaConversions,
	Wart.Option2Iterable,
	Wart.TryPartial
)
