package org.damienoreilly.tpce
import cats.data.EitherT
import cats.effect.IO
import cats.instances.list.catsStdInstancesForList
import cats.syntax.foldable.catsSyntaxNestedFoldable
import org.damienoreilly.tpce.Codecs._
import org.http4s.Status.{ClientError, Successful}
import org.http4s.Uri.Path.Segment
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Accept, Authorization}
import org.http4s.syntax.all._
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

object CompetitionEnterer extends Http4sClientDsl[IO] {

  implicit def logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def apply(implicit config: AppConfig, client: Client[IO]): IO[Unit] =
    enterCompetitions
      .map(printCompetitionResults)
      .foldF(err => Logger[IO].error(s"Something went wrong: ${handleError(err)}"), identity)

  private[tpce] def enterCompetitions(implicit config: AppConfig, client: Client[IO]): Result =
    for {
      login <- makeRequest[Token](loginRequest)
      comps <- makeRequest[List[Competition]](competitionsRequest(login.access_token))
    } yield for {
      comp <- comps
      if comp.remaining == 1 && !comp.`type`.contains("static")
    } yield (
      comp,
      makeRequest[CompetitionEntered](
        enterCompetitionRequest(
          login.access_token,
          comp.id,
          EnterCompetition(offerName = comp.name)
        )
      )
    )

  private def makeRequest[A](
      request: Request[IO]
  )(implicit decoder: EntityDecoder[IO, A], client: Client[IO]): EitherT[IO, ThreePlusError, A] =
    EitherT {
      client.run(request).use {
        case Successful(resp) =>
          resp.attemptAs[A].leftMap(err => UnknownResponse(err.message)).value
        case ClientError(resp) =>
          resp
            .attemptAs[ThreePlusError]
            .fold(decodeFailure => Left(UnknownResponse(decodeFailure.message)), err => Left(err))
        case other =>
          other.as[String].map { body =>
            Left(
              UnknownResponse(
                s"Unknown error. $request failed with status ${other.status.code} and body $body"
              )
            )
          }
      }
    }

  private def loginRequest(implicit config: AppConfig): Request[IO] =
    Method.POST(
      UrlForm(
        "grant_type" -> "password",
        "username" -> config.username,
        "password" -> config.password
      ),
      config.api.withPath(path"/core/oauth/token"),
      Authorization(BasicCredentials("clientid", "secret")),
      Accept(MediaType.application.json)
    )

  private def competitionsRequest(token: String)(implicit config: AppConfig): Request[IO] =
    Method.GET(
      config.api.withPath(path"/core/offers/competitions"),
      Authorization(Credentials.Token(AuthScheme.Bearer, token)),
      Accept(MediaType.application.json)
    )

  private def enterCompetitionRequest(token: String, offer: Int, enter: EnterCompetition)(implicit
      config: AppConfig
  ): Request[IO] = {
    val path = Uri.Path.apply(
      segments = Vector(
        Segment.apply("core"),
        Segment.apply("offers"),
        Segment.apply(offer.toString),
        Segment.apply("competitions"),
        Segment.apply("purchase")
      ),
      absolute = true
    )

    Method.PUT(
      enter,
      config.api.withPath(path),
      Authorization(Credentials.Token(AuthScheme.Bearer, token)),
      Accept(MediaType.application.json)
    )
  }

  private def printCompetitionResults: CompetitionResults => IO[Unit] = { competitionResults =>
    val results = competitionResults.map {
      case (competition, enteredOrError) =>
        enteredOrError.foldF(
          error =>
            Logger[IO].error(
              s"Entering competition: [${competition.id}] - ${competition.title} failed due to: ${handleError(error)}"
            ),
          _ => Logger[IO].info(s"Successfully entered: [${competition.id}] - ${competition.title}")
        )
    }

    if (results.isEmpty)
      Logger[IO].info("No competitions available.")
    else
      results.sequence_
  }

  private def handleError(error: ThreePlusError): String = {
    val reason = error match {
      case e: RequestError             => s"${e.error_description}, ${e.error}"
      case e: CompetitionEnteringError => s"${e.message}"
      case e: UnknownResponse          => s"${e.reason}"
    }
    s"Failed, reason: $reason"
  }

}
