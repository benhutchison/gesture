lazy val commonSettings = Seq(
  organization := "com.github.benhutchison",
  version := "0.3",
  scalaVersion := "2.12.0",
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats" % "0.8.1"
  ),
  publishArtifact in Test := false,
  pomExtra :=
    <url>https://github.com/benhutchison/gesture</url>
      <licenses>
        <license>
          <name>MIT license</name>
          <url>http://opensource.org/licenses/MIT</url>
        </license>
      </licenses>
      <scm>
        <url>git://github.com/benhutchison/gesture.git</url>
      </scm>
      <developers>
        <developer>
          <id>benhutchison</id>
          <name>Ben Hutchison</name>
          <url>https://github.com/benhutchison</url>
        </developer>
      </developers>
)

lazy val core = crossProject.in(file("./core"))
  .settings(
    name := "gesture"
  )
  .settings(commonSettings: _*).
  jvmSettings(
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.8.6" % "test"
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )

lazy val coreJS = core.js

lazy val coreJVM = core.jvm

lazy val root = project.in(file(".")).aggregate(coreJS, coreJVM).
  settings(
    publishArtifact := false,
    sonatypeProfileName := "com.github.benhutchison",
    crossScalaVersions := Seq("2.11.8", "2.12.0")
  )

lazy val demo = project.in(file("./demo"))
  .settings(
    name := "gestureDemo",
    libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % "0.9.1")
  )
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .dependsOn(coreJS)
  .aggregate(coreJS)
