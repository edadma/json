name := "JSON"

version := "0.3"

scalaVersion := "2.11.4"

scalacOptions ++= Seq( "-deprecation", "-feature", "-language:postfixOps", "-language:implicitConversions", "-language:existentials" )

incOptions := incOptions.value.withNameHashing( true )

organization := "org.funl-lang"

resolvers += Resolver.sonatypeRepo( "snapshots" )

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.11.5" % "test"

//libraryDependencies += "org.scala-lang" %% "scala-pickling" % "0.8.0-SNAPSHOT"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2"

mainClass in (Compile, run) := Some( "funl.json.JSONTest" )

//offline := true

publishMavenStyle := true

publishTo := Some( Resolver.sftp( "Hyperreal Repository", "hyperreal.ca", "/var/www/hyperreal.ca/maven2" ) )

//  val nexus = "https://oss.sonatype.org/"
//  if (isSnapshot.value)
//    Some("snapshots" at nexus + "content/repositories/snapshots")
//  else
//    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
//}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/FunL/json"))

pomExtra := (
  <scm>
    <url>git@github.com:FunL/json.git</url>
    <connection>scm:git:git@github.com:FunL/json.git</connection>
  </scm>
  <developers>
    <developer>
      <id>edadma</id>
      <name>Edward A. Maxedon, Sr.</name>
      <url>http://funl-lang.org</url>
    </developer>
  </developers>)
