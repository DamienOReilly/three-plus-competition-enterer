package org.damienoreilly.tpce

import cats.effect._
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.Implicits.global

case class AppConfig(
  username: String,
  password: String,
  api: Uri
)

object ThreePlusEntererApp extends IOApp with StrictLogging {

  override def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO](global).resource
      .use(start)
      .as(ExitCode.Success)

  private def start(client: Client[IO]) = {
    implicit val c: Client[IO] = client

    import pureconfig.module.http4s._
    val config = pureconfig.loadConfigOrThrow[AppConfig]

    val service = new CometitionEntererService(config)

    service.enterCompetitions.value.flatMap(
      _.fold(
        err => IO.raiseError(ThreePlusEntererException(handleError(err))),
        suc => handleCompetitionResults(suc)
      )
    )
  }

  private def handleCompetitionResults(suc: CompetitionResults) = {
    suc
      .traverse(compAndResult => compAndResult._2.value.map(comp => (compAndResult._1, comp)))
      .map(
        _.map {
          case (c: Competition, Left(e))  => s"Enteering competition [${c.id}] - ${c.title} failed ${handleError(e)}"
          case (c: Competition, Right(_)) => s"Successfully entered ${c.title}"
        }
      )
      .flatMap(x => IO(logger.info(x.mkString("\n"))))
  }

  private def handleError(e: ThreePlusError) = {
    e match {
      case e: RequestError             => s"Failed, reason ${e.error_description}, ${e.error}"
      case e: FatalError               => s"Failed, reason ${e.message}"
      case e: CompetitionEnteringError => s"Failed, reason ${e.message}"
      case e: UnknownResponse          => s"Failed, reason ${e.reason}"
    }
  }
}
