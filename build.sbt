lazy val commonSettings = Seq(
  organization := "com.github.benhutchison",
  version := "0.5",
  scalaVersion := "2.13.7",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
  ),
)

lazy val core = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).in(file("core"))
  .settings(
    name := "gesture",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core" % "2.7.0",
      "org.scalameta" %% "munit" % "0.7.29" % Test,
    ),
  )
  .settings(commonSettings: _*)

lazy val root = project.in(file(".")).aggregate(core.jvm).
  settings(
    skip in publish := true,
  )

lazy val demo = project.in(file("./demo"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "gestureDemo",
    libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % "2.1.0"),
  )
  .settings(commonSettings: _*)
  .aggregate(core.js)
