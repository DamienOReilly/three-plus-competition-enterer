package org.damienoreilly.tpce
import cats.data.EitherT
import cats.effect.IO
import cats.instances.list.catsStdInstancesForList
import cats.syntax.foldable.catsSyntaxNestedFoldable
import cats.syntax.traverse.toTraverseOps
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.chrisdavenport.log4cats.{Logger, SelfAwareStructuredLogger}
import org.damienoreilly.tpce.Codecs._
import org.http4s.Status.{ClientError, Successful}
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Accept, Authorization}

object CompetitionEnterer extends Http4sClientDsl[IO] {

  implicit def logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def apply(implicit config: AppConfig, client: Client[IO]): IO[Unit] =
    enterCompetitions
      .map(parseCompetitionResults)
      .foldF(err => Logger[IO].error(s"Something went wrong: ${handleError(err)}"), identity)

  private[tpce] def enterCompetitions(implicit
                                      config: AppConfig,
                                      client: Client[IO]): Result =
    for {
      login <- makeRequest[Token](loginRequest)
      comps <- makeRequest[List[Competition]](competitionsRequest(login.access_token))
    } yield
      for {
        comp <- comps
        if comp.remaining == 1 && !comp.`type`.contains("static")
      } yield
        (
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
      request: IO[Request[IO]]
  )(implicit decoder: EntityDecoder[IO, A], client: Client[IO]): EitherT[IO, ThreePlusError, A] =
    EitherT(
      request.flatMap(request =>
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
      })
    )

  private def loginRequest(implicit config: AppConfig): IO[Request[IO]] =
    Method.POST(
      UrlForm(
        "grant_type" -> "password",
        "username" -> config.username,
        "password" -> config.password
      ),
      config.api.withPath("/core/oauth/token"),
      Authorization(BasicCredentials("clientid", "secret")),
      Accept(MediaType.application.json)
    )

  private def competitionsRequest(token: String)(implicit config: AppConfig): IO[Request[IO]] =
    Method.GET(
      config.api.withPath("/core/offers/competitions"),
      Authorization(Credentials.Token(AuthScheme.Bearer, token)),
      Accept(MediaType.application.json)
    )

  private def enterCompetitionRequest(token: String, offer: Int, enter: EnterCompetition)(
      implicit
      config: AppConfig): IO[Request[IO]] =
    Method.PUT(
      enter,
      config.api.withPath(s"/core/offers/$offer/competitions/purchase"),
      Authorization(Credentials.Token(AuthScheme.Bearer, token)),
      Accept(MediaType.application.json)
    )

  private def parseCompetitionResults: CompetitionResults => IO[Unit] = { competitionResults =>
    competitionResults
      .traverse { result =>
        result._2
          .map(_ =>
            Logger[IO].info(
              s"Successfully entered: [${result._1.id}] - ${result._1.title.getOrElse("[Unknown competition]")}"))
          .leftMap { error =>
            Logger[IO].error(
              s"Entering competition: [${result._1.id}] - ${result._1.title.getOrElse(
                "[Unknown competition]")} failed due to: ${handleError(error)}"
            )
          }
      }
      .map(_.sequence_)
      .foldF(identity, identity)
  }

  private def handleError(e: ThreePlusError): String =
    "Failed, reason " + {
      e match {
        case e: RequestError             => s"${e.error_description}, ${e.error}"
        case e: FatalError               => s"${e.message}"
        case e: CompetitionEnteringError => s"${e.message}"
        case e: UnknownResponse          => s"${e.reason}"
      }
    }

}
