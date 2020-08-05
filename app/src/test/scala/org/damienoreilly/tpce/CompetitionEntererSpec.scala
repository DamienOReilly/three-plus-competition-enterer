package org.damienoreilly.tpce

import cats.effect.IO
import org.damienoreilly.tpce.TestData._
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{HttpApp, Response}
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import weaver.SimpleIOSuite

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

  simpleTest(
    "enterCompetitions should return RequestError if Three Plus API cannot serve the request") {

    val resp = HttpApp.pure(Response[IO](BadRequest).withEntity(badCredentialsResponse))
    val client: Client[IO] = Client.fromHttpApp(resp)

    val expectedResponse = RequestError("invalid_grant", "Bad credentials")

    val result = CompetitionEnterer.enterCompetitions(config, client)

    result.value.map { response =>
      expect(response.left.value.equals(expectedResponse))
    }
  }

  simpleTest(
    "enterCompetitions should return FatalError if there is a problem with the Three Plus API") {

    val resp = HttpApp.pure(Response[IO](BadRequest).withEntity(fatalErrorResponse))
    val client: Client[IO] = Client.fromHttpApp(resp)

    val expectedResponse = FatalError(
      1539970430277L,
      405,
      "Method Not Allowed",
      "java.lang.Exception",
      "Something bad happened",
      "/some/path"
    )

    val result = CompetitionEnterer.enterCompetitions(config, client)

    result.value.map { response =>
      expect(response.left.value.equals(expectedResponse))
    }
  }

  simpleTest(
    "enterCompetitions should return UnknownResponse if they payload from Three Plus API is not recognised") {

    val resp = HttpApp.pure(Response[IO](BadRequest).withEntity("Some unexpected body"))
    val client: Client[IO] = Client.fromHttpApp(resp)

    val expectedResponse = UnknownResponse("Malformed message body: Invalid JSON")

    val result = CompetitionEnterer.enterCompetitions(config, client)

    result.value.map { response =>
      expect(response.left.value.equals(expectedResponse))
    }
  }

  simpleTest("enterCompetitions should return list of competitions entered with results") {

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

    val client: Client[IO] = Client.fromHttpApp(resp)

    // Its a bit clunky dealing with the types and using weaver test-suite. There might be a nicer way to do this.
    for {
      result <- CompetitionEnterer.enterCompetitions(config, client).value
      comp <- IO(
        result.fold(_ => List.empty[IO[Either[ThreePlusError, CompetitionEntered]]],
                    _.map(_._2).map(_.value)))
      first = comp.head
      second = comp(1)
      third = comp(2)
      _ <- expect(comp.size == 3).failFast
      _ <- first.flatMap(item =>
        expect(item.equals(Right(CompetitionEntered(None, None, None, None, None)))).failFast)
      _ <- second.flatMap(item =>
        expect(item.equals(Right(CompetitionEntered(None, None, None, None, None)))).failFast)
      _ <- third.flatMap(item =>
        expect(item.equals(Left(CompetitionEnteringError("subscriber.offer.limit.reached", 411)))).failFast)
    } yield success
  }

  simpleTest("enterCompetitions should not try enter competitions with property remaining != 1") {

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
        result.fold(_ => List.empty[IO[Either[ThreePlusError, CompetitionEntered]]],
                    _.map(_._2).map(_.value)))
      _ <- expect(comp.isEmpty).failFast

    } yield success
  }

  simpleTest("enterCompetitions should not try enter competitions with type 'static'") {

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
        result.fold(_ => List.empty[IO[Either[ThreePlusError, CompetitionEntered]]],
                    _.map(_._2).map(_.value)))
      _ <- expect(comp.isEmpty).failFast

    } yield success
  }

}
