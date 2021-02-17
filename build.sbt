lazy val json = crossProject(JSPlatform, JVMPlatform/*, NativePlatform*/)/*.crossType(CrossType.Pure)*/.in(file(".")).
  settings(
    name := "json",
    version := "0.8.3",
    scalaVersion := "2.13.4",
    scalacOptions ++=
      Seq(
        "-deprecation", "-feature", "-unchecked",
        "-language:postfixOps", "-language:implicitConversions", "-language:existentials", "-language:dynamics",
        "-Xasync"
      ),
    organization := "xyz.hyperreal",
    resolvers += "Hyperreal Repository" at "https://dl.bintray.com/edadma/maven",
    mainClass := Some("xyz.hyperreal.json.Main"),
    libraryDependencies ++=
      Seq(
        "xyz.hyperreal" %%% "char-reader" % "0.1.9"
      ),
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.3" % "test",
    publishMavenStyle := true,
    publishArtifact in Test := false,
    licenses += "ISC" -> url("https://opensource.org/licenses/ISC")
  ).
  jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.0.0" % "provided",
  ).
  //  nativeSettings(
  //    nativeLinkStubs := true
  //  ).
  jsSettings(
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
//    Test / scalaJSUseMainModuleInitializer := true,
//    Test / scalaJSUseTestModuleInitializer := false,
    Test / scalaJSUseMainModuleInitializer := false,
    Test / scalaJSUseTestModuleInitializer := true,
    scalaJSUseMainModuleInitializer := true,
  )
