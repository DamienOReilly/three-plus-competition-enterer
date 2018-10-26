package org.damienoreilly.tpce
import cats.data.EitherT
import cats.effect.IO
import org.damienoreilly.tpce.Codecs._
import org.http4s.Status.{ClientError, Successful}
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Accept, Authorization}

class CometitionEntererService(config: AppConfig) extends Http4sClientDsl[IO] {

  def enterCompetitions(implicit client: Client[IO]): Result = {
    for {
      login <- makeRequest[Token](loginRequest)
      comps <- makeRequest[List[Competition]](competitionsRequest(login.access_token))
    } yield
      for {
        comp <- comps
        if comp.remaining == 1
      } yield
        (
          comp,
          makeRequest[CompetitionEntered](
            enterCompetitionRequest(login.access_token, comp.id, EnterCompetition(offerName = comp.name))
          )
        )
  }

  private def makeRequest[A](request: IO[Request[IO]])(implicit decoder: EntityDecoder[IO, A], client: Client[IO]) =
    EitherT(client.fetch(request) {
      case Successful(resp) => {
        resp.attemptAs[A].leftMap(x => UnknownResponse(x.message)).value
      }
      case ClientError(resp) => resp.attemptAs[ThreePlusError].fold(l => Left(UnknownResponse(l.message)), r => Left(r))
      case other =>
        other
          .as[String]
          .map(
            body =>
              Left(
                UnknownResponse(s"Unknown error. $request failed with status ${other.status.code} and body $body")
            )
          )
    })

  private val loginRequest = Method.POST(
    config.api.withPath("/core/oauth/token"),
    UrlForm(
      "grant_type" -> "password",
      "username" -> config.username,
      "password" -> config.password
    ),
    Authorization(BasicCredentials("clientid", "secret")),
    Accept(MediaType.application.json)
  )

  private def competitionsRequest(token: String) = Method.GET(
    config.api.withPath("/core/offers/competitions"),
    Authorization(Credentials.Token(AuthScheme.Bearer, token)),
    Accept(MediaType.application.json)
  )

  private def enterCompetitionRequest(token: String, offer: Int, enter: EnterCompetition) = Method.PUT(
    config.api.withPath(s"/core/offers/$offer/competitions/purchase"),
    enter,
    Authorization(Credentials.Token(AuthScheme.Bearer, token)),
    Accept(MediaType.application.json)
  )

}
