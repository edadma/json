name := "JSON"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.1"

scalacOptions ++= Seq( "-deprecation", "-feature", "-language:postfixOps", "-language:implicitConversions", "-language:existentials" )

incOptions := incOptions.value.withNameHashing( true )

organization := "org.funl-lang"

target := file( "/home/ed/target/" + moduleName.value )

resolvers += Resolver.sonatypeRepo( "snapshots" )

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.3" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"

//libraryDependencies += "org.scala-lang" %% "scala-pickling" % "0.8.0-SNAPSHOT"

//libraryDependencies += "org.scala-lang" % "scala-swing" % scalaVersion.value

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1"

//libraryDependencies += "org.scaloid" %% "scaloid" % "3.2-8"

//libraryDependencies += "org.clapper" %% "argot" % "1.0.1"

libraryDependencies ++= Seq(
//	"org.postgresql" % "postgresql" % "9.2-1004-jdbc4"
//	"mysql" % "mysql-connector-java" % "5.1.29"
//	"org.mongodb" %% "casbah" % "2.6.3"
//	"org.antlr" % "stringtemplate" % "4.0.2"
	)

libraryDependencies ++= Seq(
//	"local" %% "LOCAL_PROJECT" % "0.1"
	)
	
//mainClass in (Compile, packageBin) := Some( "myproject.MyMain" )

mainClass in (Compile, run) := Some( "funl.json.JSONReaderTest" )

//offline := true
