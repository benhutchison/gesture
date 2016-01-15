import sbt._
import sbt.Keys._

import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.cross.CrossProject



lazy val commonSettings = Seq(
  organization := "com.github.benhutchison",
  version := "0.1",
  scalaVersion := "2.11.6",
  libraryDependencies ++= Seq(
    "org.spire-math" %%% "cats" % "0.3.0"
  ),
  publishTo <<= version { (v: String) =>
    Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
  },
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
) ++ xerial.sbt.Sonatype.sonatypeSettings

lazy val core = crossProject.in(file("./core"))
  .settings(
    name := "gesture"
  )
  .settings(commonSettings: _*).
  jvmSettings(
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.7" % "test"
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )

lazy val coreJS = core.js

lazy val coreJVM = core.jvm

lazy val demo = project.in(file("./demo"))
  .settings(
    name := "gestureDemo",
    libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % "0.8.0")
  )
  .settings(commonSettings: _*)
  .dependsOn(coreJS)
  .aggregate(coreJS)
  .enablePlugins(ScalaJSPlugin)
