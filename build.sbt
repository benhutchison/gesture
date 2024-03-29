inThisBuild(List(
  organization := "com.github.benhutchison",
  version := "0.6",
  scalaVersion := "3.1.1",
  crossScalaVersions := Seq("3.1.1"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
  ),
  homepage := Some(url("https://github.com/benhutchison/gesture")),
  developers := List(
    Developer("benhutchison", "Ben Hutchison", "brhutchison@gmail.com", url = url("https://github.com/benhutchison"))
  ),
  licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-projectmatrix/blob/master/LICENSE")),
  sonatypeProfileName := "benhutchison",
))


lazy val core = project.in(file("core"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "gesture",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.1.0",
      "org.typelevel" %%% "cats-core" % "2.7.0",
      "org.scalameta" %%% "munit" % "0.7.29" % Test,
    ),
  )

lazy val root = project.in(file(".")).aggregate(core).
  settings(
    publish / skip := true,
  )

lazy val demo = project.in(file("./demo"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "gestureDemo",
    scalaJSUseMainModuleInitializer := true,
  )
  .dependsOn(core)
  .aggregate(core)

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(RefPredicate.StartsWith(Ref.Tag("v")))

ThisBuild / githubWorkflowGeneratedUploadSteps ~= { steps =>
  val mkdirStep = steps.headOption match {
    case Some(WorkflowStep.Run(command :: _, _, _, _, _, _)) =>
      WorkflowStep.Run(
        commands = List(command.replace("tar cf targets.tar", "mkdir -p")),
        name = Some("Make target directories")
      )
    case _ => sys.error("Can't generate make target dirs workflow step")
  }
  mkdirStep +: steps
}

ThisBuild / githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release")))

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)
