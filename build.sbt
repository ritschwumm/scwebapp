name			:= "scwebapp"

organization	:= "de.djini"

version			:= "0.6.0"

scalaVersion	:= "2.9.2"

libraryDependencies	++= Seq(
	"de.djini"		%%	"scutil"		% "0.9.0"	% "compile",
	"javax.servlet"	%	"servlet-api"	% "2.5"		% "provided"
)

scalacOptions	++= Seq("-deprecation", "-unchecked")
