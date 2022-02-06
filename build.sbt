enablePlugins(ScalaJSPlugin)

lazy val commonSettings = Seq(
  organization := "com.github.benhutchison",
  version := "0.5",
  scalaVersion := "2.13.7",
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core" % "2.7.0"
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
  ),
)

lazy val core = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).in(file("core"))
  .settings(
    name := "gesture"
  )
  .settings(commonSettings: _*).
  jvmSettings(
    libraryDependencies ++= Seq(
       "org.scalameta" %% "munit" % "0.7.29" % Test
    ),
  )

lazy val coreJS = core.js

lazy val coreJVM = core.jvm

lazy val root = project.in(file(".")).aggregate(coreJS, coreJVM).
  settings(
    skip in publish := true,
  )

lazy val demo = project.in(file("./demo"))
  .settings(
    name := "gestureDemo",
    libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % "2.1.0"),
  )
  .settings(commonSettings: _*)
  .dependsOn(coreJS)
  .aggregate(coreJS)
