name := "ThreePlusCompetitionApp"

import Versions._

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe"        % http4sVersion,

  "io.circe" %% "circe-generic"        % circeVersion,
  "io.circe" %% "circe-literal"        % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,

  "com.github.pureconfig" %% "pureconfig"        % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-http4s" % pureConfigVersion,

  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,

  "ch.qos.logback" % "logback-classic" % logbackVersion,

  "org.scalatest" %% "scalatest" % scalaTestVersion % "test"

)
