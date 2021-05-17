name := "ThreePlusCompetitionApp"

import Versions._

graalVMNativeImageOptions ++= Seq(
  "--static",
  "--no-server",
  "--verbose",
  "--no-fallback",
  "--allow-incomplete-classpath",
  "--install-exit-handlers",
  "--initialize-at-build-time=" +
    "scala.math.BigDecimal$,scala.collection.immutable.Node$," +
    "scala.collection.immutable.Vector0$," +
    "scala.runtime.Statics$VM," +
    "scala.collection.Iterable$," +
    "scala.collection.immutable.IndexedSeq$," +
    "scala.concurrent.duration.Duration$," +
    "scala.Predef$," +
    "scala.collection.immutable.List$," +
    "scala.collection.immutable.Vector$," +
    "scala.collection.immutable.Seq$," +
    "scala.collection.immutable.Iterable$," +
    "scala.reflect.ManifestFactory$," +
    "scala.reflect.ClassTag$," +
    "scala.collection.immutable.LazyList$," +
    "scala.package$," +
    "scala.reflect.Manifest$,scala.math.BigInt$",
  "-H:+RemoveSaturatedTypeFlows",
  "-H:+TraceServiceLoaderFeature",
  "-H:+ReportUnsupportedElementsAtRuntime",
  "-H:+ReportExceptionStackTraces",
  "-H:Log=registerResource:",
  "--enable-https"
)

libraryDependencies ++= Seq(

  "ch.qos.logback" % "logback-classic" % logbackVersion,

  "com.github.pureconfig" %% "pureconfig"        % pureConfigVersion,
//  "com.github.pureconfig" %% "pureconfig-http4s" % pureConfigVersion,

  "io.circe" %% "circe-generic"        % circeVersion,
  "io.circe" %% "circe-literal"        % circeVersion,
  "io.circe" %% "circe-parser"         % circeVersion % Test,

  "org.graalvm.nativeimage" % "svm"      % graalvmVersion % Provided,

  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe"        % http4sVersion,

  "org.scalameta" %% "svm-subs" % svmSubsVersion,

  "org.typelevel" %% "cats-effect"      % catsEffectVersion,

  "org.typelevel" %% "log4cats-core"    % log4catsVersion,
  "org.typelevel" %% "log4cats-slf4j"   % log4catsVersion,

  "com.disneystreaming" %% "weaver-cats" % weaverFrameworkVersion % Test,

  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
)
