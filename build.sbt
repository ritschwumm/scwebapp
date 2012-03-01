name			:= "scwebapp"

organization	:= "de.djini"

version			:= "0.0.2"

scalaVersion	:= "2.9.1"

//publishArtifact in (Compile, packageBin)	:= false

publishArtifact in (Compile, packageDoc)	:= false

publishArtifact in (Compile, packageSrc)	:= false

libraryDependencies	++= Seq(
	"de.djini"		%%	"scutil"		% "0.0.5"	% "compile",
	"javax.servlet"	%	"servlet-api"	% "2.5"		% "provided"
)

scalacOptions	++= Seq("-deprecation", "-unchecked")
