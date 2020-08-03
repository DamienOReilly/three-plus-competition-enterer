package org.damienoreilly.tpce

import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.StrictLogging
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object ThreePlusCompetitionApp extends IOApp with StrictLogging {

  override def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO](global).resource
      .use(start)
      .as(ExitCode.Success)

  private def start(client: Client[IO]): IO[Unit] = {

    import pureconfig.module.http4s._
    import pureconfig.generic.auto._
    val config = pureconfig.ConfigSource.default.loadOrThrow[AppConfig]

    CompetitionEnterer(config, client)
  }
}
