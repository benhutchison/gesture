ThisBuild / organization := "com.github.benhutchison"
ThisBuild /  version := "0.5"
ThisBuild /  scalaVersion := "2.13.7"
ThisBuild /  crossScalaVersions := Seq("2.13.7")
ThisBuild / scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
  )
ThisBuild / sonatypeProfileName := "benhutchison"


lazy val core = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).in(file("core"))
  .settings(
    name := "gesture",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core" % "2.7.0",
      "org.scalameta" %% "munit" % "0.7.29" % Test,
    ),
  )

lazy val root = project.in(file(".")).aggregate(core.jvm).
  settings(
    publish / skip := true,
  )

lazy val demo = project.in(file("./demo"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "gestureDemo",
    libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % "2.1.0"),
  )
  .aggregate(core.js)

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
