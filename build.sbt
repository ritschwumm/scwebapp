name			:= "scwebapp"

organization	:= "de.djini"

version			:= "0.8.0"

scalaVersion	:= "2.9.2"

libraryDependencies	++= Seq(
	"de.djini"		%%	"scutil"			% "0.11.0"	% "compile",
	"javax.servlet"	%	"javax.servlet-api"	% "3.0.1"	% "provided"
)

scalacOptions	++= Seq("-deprecation", "-unchecked")
