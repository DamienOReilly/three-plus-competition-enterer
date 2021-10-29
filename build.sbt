name := "ThreePlusCompetitionEnterer"

lazy val commonSettings = Seq(
  version := "0.7",
  organization := "org.damienoreilly.tpce",
  assembly / test := {},
  scalaVersion := "2.13.6",
  testFrameworks += new TestFramework("weaver.framework.CatsEffect")
)

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val app = (project in file("app"))
  .settings(commonSettings: _*)
  .settings(
    assembly / mainClass := Some("org.damienoreilly.tpce.ThreePlusCompetitionApp")
  )
  .settings(assembly / assemblyJarName := "ThreePlusCompetitionApp.jar")
  .enablePlugins(JavaAppPackaging, GraalVMNativeImagePlugin)
