package org.damienoreilly.tpce

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import pureconfig.generic.auto._


object ThreePlusCompetitionApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeClientBuilder.apply[IO]
      .resource
      .use(start)
      .as(ExitCode.Success)
  }

  private def start(client: Client[IO]): IO[Unit] = {
    val config = pureconfig.ConfigSource.default.loadOrThrow[AppConfig]
    CompetitionEnterer.apply(config, client)
  }
}
