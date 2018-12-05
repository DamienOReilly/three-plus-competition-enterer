package org.damienoreilly.tpce

import cats.effect.IO
import org.damienoreilly.tpce.TestData._
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpApp, Response, Uri}
import org.scalatest.EitherValues._
import org.scalatest.{Matchers, WordSpecLike}

class CompetitionEntererServiceTest extends WordSpecLike with Http4sDsl[IO] with Matchers {

  private val config =
    AppConfig(
      "name",
      "pass",
      Uri.uri("http://localhost")
    )

  "CompetitionEntererService" should {

    "return RequestError if Three Plus API cannot serve the request" in {

      val resp = HttpApp.pure(Response[IO](BadRequest).withEntity(badCredentialsResponse))
      implicit val client: Client[IO] = Client.fromHttpApp(resp)

      val expectedResponse = RequestError("invalid_grant", "Bad credentials")

      val service = new CompetitionEntererService(config)
      service.enterCompetitions.value.unsafeRunSync.left.value shouldBe expectedResponse

    }

    "return FatalError if there is a problem with the Three Plus API" in {

      val resp = HttpApp.pure(Response[IO](BadRequest).withEntity(fatalErrorResponse))
      implicit val client: Client[IO] = Client.fromHttpApp(resp)

      val expectedResponse = FatalError(
        1539970430277L,
        405,
        "Method Not Allowed",
        "java.lang.Exception",
        "Something bad happened",
        "/some/path"
      )

      val service = new CompetitionEntererService(config)
      service.enterCompetitions.value.unsafeRunSync.left.value shouldBe expectedResponse

    }

    "return UnknownResponse if they payload from Three Plus API is not recognised" in {

      val resp = HttpApp.pure(Response[IO](BadRequest).withEntity("Some unexpected body"))
      implicit val client: Client[IO] = Client.fromHttpApp(resp)

      val expectedResponse = UnknownResponse("Malformed message body: Invalid JSON")

      val service = new CompetitionEntererService(config)
      service.enterCompetitions.value.unsafeRunSync.left.value shouldBe expectedResponse

    }

    "return list of competitions entered with results" in {

      val resp = HttpApp[IO] {
        case POST -> Root / "core" / "oauth" / "token" =>
          Ok(loginSuccessResponse)
        case GET -> Root / "core" / "offers" / "competitions" =>
          Ok(competitionsResponse)
        case PUT -> Root / "core" / "offers" / ("111" | "222") / "competitions" / "purchase" =>
          Ok(enteredCompetitionResponse)
        case PUT -> Root / "core" / "offers" / "444" / "competitions" / "purchase" =>
          LengthRequired(enterCompetitionErrorResponse)
        case _ => NotFound()
      }

      implicit val client: Client[IO] = Client.fromHttpApp(resp)

      val service = new CompetitionEntererService(config)
      val compResults = service.enterCompetitions.value.unsafeRunSync.right.value

      compResults.size shouldBe 3 //  4 comps , one is already entered, so we shouldn't attempt to enter again.
      compResults(0)._2.value.unsafeRunSync.right.value shouldBe CompetitionEntered(None, None, None, None, None)
      compResults(1)._2.value.unsafeRunSync.right.value shouldBe CompetitionEntered(None, None, None, None, None)
      compResults(2)._2.value.unsafeRunSync.left.value shouldBe CompetitionEnteringError(
        "subscriber.offer.limit.reached",
        411
      )

    }

  }

}
