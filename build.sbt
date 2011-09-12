name			:= "scwebapp"

organization	:= "de.djini"

version			:= "0.0.1"

scalaVersion	:= "2.9.0-1"

//publishArtifact in (Compile, packageBin)	:= false

publishArtifact in (Compile, packageDoc)	:= false

publishArtifact in (Compile, packageSrc)	:= false

libraryDependencies	++= Seq(
	"de.djini"		%%	"scutil"		% "0.0.4"	% "compile",
	"javax.servlet"	%	"servlet-api"	% "2.5"		% "provided"
)

scalacOptions	++= Seq("-deprecation", "-unchecked")
