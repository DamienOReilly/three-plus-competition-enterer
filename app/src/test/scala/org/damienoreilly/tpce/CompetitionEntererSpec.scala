package org.damienoreilly.tpce

import cats.effect.IO
import org.damienoreilly.tpce.TestData._
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Headers, HttpApp, Media, MediaType, Response}
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import weaver.SimpleIOSuite

import java.nio.charset.StandardCharsets

object CompetitionEntererSpec
    extends SimpleIOSuite
    with Http4sDsl[IO]
    with EitherValues
    with Matchers {

  private val config =
    AppConfig(
      "name",
      "pass",
      uri"http://localhost"
    )

  private def media(data: String) =
    Media.apply[IO](
      fs2.Stream.emits(data.getBytes(StandardCharsets.UTF_8)),
      Headers(`Content-Type`.apply(MediaType.application.json))
    )

  test("tokenEntityDecoder should parse a token on the login success") {
    val expected = Token("dummytoken")
    Codecs.tokenEntityDecoder
      .decode(media(TestData.loginSuccessResponse), strict = true)
      .value
      .map { result =>
        expect(result.value.equals(expected))
      }
  }

  test("errorEntityDecoder should parse the error on the login failure") {
    val expected = RequestError("invalid_grant", "Bad credentials")
    Codecs.errorEntityDecoder
      .decode(media(TestData.loginFailureResponse), strict = true)
      .value
      .map { result =>
        expect(result.value.equals(expected))
      }
  }

  test("errorEntityDecoder should parse the error on API errors") {
    val expected = CompetitionEnteringError("Something bad happened", 405)
    Codecs.errorEntityDecoder
      .decode(media(TestData.fatalErrorResponse), strict = true)
      .value
      .map { result =>
        expect(result.value.equals(expected))
      }
  }

  test("competitionEnteredDecoder should parse the error on API errors") {
    val expected = CompetitionEntered(None, None, None, None, None)
    Codecs.competitionEnteredDecoder
      .decode(media(TestData.competitionEnteredResponse), strict = true)
      .value
      .map { result =>
        expect(result.value.equals(expected))
      }
  }

  test("competitionEntityDecoder should parse a token from the login response") {
    val expected = List(
      Competition(111, "Win a €250 Voucher", "classic", "[COMPETITION] Win a €250 Voucher", 1),
      Competition(222, "Win a €250 Voucher", "classic", "[COMPETITION] Win a €500 Voucher", 1),
      Competition(444, "Win a €1000 Voucher", "classic", "[COMPETITION] Win a €1000 Voucher", 1)
    )

    Codecs.competitionEntityDecoder
      .decode(
        Media.apply(
          fs2.Stream.emits(TestData.competitionsResponse.getBytes(StandardCharsets.UTF_8)),
          Headers(`Content-Type`.apply(MediaType.application.json))
        ),
        strict = true
      )
      .value
      .map { result =>
        expect(result.value.equals(expected))
      }
  }

  test("enterCompetitions should return RequestError if Three Plus API cannot serve the request") {

    val resp = HttpApp.pure(Response[IO](BadRequest).withEntity(loginFailureResponse))
    val client: Client[IO] = Client.fromHttpApp(resp)

    val expectedResponse = RequestError("invalid_grant", "Bad credentials")

    val result = CompetitionEnterer.enterCompetitions(config, client)

    result.value.map { response =>
      expect(response.left.value.equals(expectedResponse))
    }
  }

  test(
    "enterCompetitions should return UnknownResponse if they payload from Three Plus API is not recognised"
  ) {

    val resp = HttpApp.pure(Response[IO](BadRequest).withEntity("Some unexpected body"))
    val client: Client[IO] = Client.fromHttpApp(resp)

    val expectedResponse = UnknownResponse("Malformed message body: Invalid JSON")

    val result = CompetitionEnterer.enterCompetitions(config, client)

    result.value.map { response =>
      expect(response.left.value.equals(expectedResponse))
    }
  }

  test("enterCompetitions should return list of competitions entered with results") {

    val resp = HttpApp[IO] {
      case POST -> Root / "core" / "oauth" / "token" =>
        Ok(loginSuccessResponse)
      case GET -> Root / "core" / "offers" / "competitions" =>
        Ok(competitionsResponse)
      case PUT -> Root / "core" / "offers" / ("111" | "222") / "competitions" / "purchase" =>
        Ok(competitionEnteredResponse)
      case PUT -> Root / "core" / "offers" / "444" / "competitions" / "purchase" =>
        LengthRequired(enterCompetitionErrorResponse)
      case _ => NotFound()
    }

    val client: Client[IO] = Client.fromHttpApp(resp)

    // Its a bit clunky dealing with the types and using weaver test-suite. There might be a nicer way to do this.
    for {
      result <- CompetitionEnterer.enterCompetitions(config, client).value
      comp <- IO(
        result.fold(
          _ => List.empty[IO[Either[ThreePlusError, CompetitionEntered]]],
          _.map(_._2).map(_.value)
        )
      )
      first = comp.head
      second = comp(1)
      third = comp(2)
      _ <- expect(comp.size == 3).failFast
      _ <- first.flatMap(item =>
        expect(item.equals(Right(CompetitionEntered(None, None, None, None, None)))).failFast
      )
      _ <- second.flatMap(item =>
        expect(item.equals(Right(CompetitionEntered(None, None, None, None, None)))).failFast
      )
      _ <- third.flatMap(item =>
        expect(
          item.equals(Left(CompetitionEnteringError("subscriber.offer.limit.reached", 411)))
        ).failFast
      )
    } yield success
  }

  test("enterCompetitions should not try enter competitions with property remaining != 1") {

    val resp = HttpApp[IO] {
      case POST -> Root / "core" / "oauth" / "token" =>
        Ok(loginSuccessResponse)
      case GET -> Root / "core" / "offers" / "competitions" =>
        Ok(remainingIsZeroCompetitions)
      case _ => NotFound()
    }

    val client: Client[IO] = Client.fromHttpApp(resp)

    for {
      result <- CompetitionEnterer.enterCompetitions(config, client).value
      comp <- IO(
        result.fold(
          _ => List.empty[IO[Either[ThreePlusError, CompetitionEntered]]],
          _.map(_._2).map(_.value)
        )
      )
      _ <- expect(comp.isEmpty).failFast

    } yield success
  }

  test("enterCompetitions should not try enter competitions with type 'static'") {

    val resp = HttpApp[IO] {
      case POST -> Root / "core" / "oauth" / "token" =>
        Ok(loginSuccessResponse)
      case GET -> Root / "core" / "offers" / "competitions" =>
        Ok(staticCompetition)
      case _ => NotFound()
    }

    val client: Client[IO] = Client.fromHttpApp(resp)

    for {
      result <- CompetitionEnterer.enterCompetitions(config, client).value
      comp <- IO(
        result.fold(
          _ => List.empty[IO[Either[ThreePlusError, CompetitionEntered]]],
          _.map(_._2).map(_.value)
        )
      )
      _ <- expect(comp.isEmpty).failFast

    } yield success
  }

}
