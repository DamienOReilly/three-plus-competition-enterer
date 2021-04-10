name := "ThreePlusCompetitionEnterer"

lazy val commonSettings = Seq(
  version := "0.5",
  organization := "org.damienoreilly.tpce",
  scalaVersion := "2.13.5",
  test in assembly := {},
  testFrameworks += new TestFramework("weaver.framework.CatsEffect")
)

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val app = (project in file("app"))
  .settings(commonSettings: _*)
  .settings(
    mainClass in assembly := Some("org.damienoreilly.tpce.ThreePlusCompetitionApp")
  )
  .settings(assemblyJarName in assembly := "ThreePlusCompetitionApp.jar")
  .enablePlugins(JavaAppPackaging, GraalVMNativeImagePlugin)
