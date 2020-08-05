name := "ThreePlusCompetitionEnterer"

lazy val commonSettings = Seq(
  version := "0.4",
  organization := "org.damienoreilly.tpce",
  scalaVersion := "2.13.3",
  test in assembly := {},
  testFrameworks += new TestFramework("weaver.framework.TestFramework")
)

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val app = (project in file("app"))
  .settings(commonSettings: _*)
  .settings(
    mainClass in assembly := Some("org.damienoreilly.tpce.ThreePlusCompetitionApp")
  )
  .settings(assemblyJarName in assembly := "ThreePlusCompetitionApp.jar")
  .enablePlugins(JavaAppPackaging, GraalVMNativeImagePlugin)
