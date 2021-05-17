package org.damienoreilly.tpce

import cats.effect.IO
import io.circe.Decoder
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder, Uri}

sealed trait ThreePlusError extends RuntimeException

case class UnknownResponse(reason: String) extends ThreePlusError

case class RequestError(
    error: String,
    error_description: String
) extends ThreePlusError

case class CompetitionEnteringError(
    message: String,
    status: Int
) extends ThreePlusError

case class Token(
    access_token: String
)

case class Competition(
    id: Int,
    title: String,
    `type`: String,
    name: String,
    remaining: Int
)

case class CompetitionEntered(
    voucher: Option[String],
    expirationDate: Option[String],
    partnerName: Option[String],
    purchaseDate: Option[String],
    voucherRedeemdate: Option[String]
)

case class EnterCompetition(
    fromWeb: Boolean = true,
    offerQuantity: Int = 1,
    sendSMS: Boolean = true,
    offerName: String
)

case class AppConfig(
    username: String,
    password: String,
    api: Uri
)

object Codecs {
  implicit val tokenEntityDecoder: EntityDecoder[IO, Token] = jsonOf[IO, Token]
  implicit val competitionEntityDecoder: EntityDecoder[IO, List[Competition]] =
    jsonOf[IO, List[Competition]]
  implicit val competitionEnteredDecoder: EntityDecoder[IO, CompetitionEntered] =
    jsonOf[IO, CompetitionEntered]
  implicit val enterEncoder: EntityEncoder[IO, EnterCompetition] =
    jsonEncoderOf[IO, EnterCompetition]

  implicit val requestErrorDecoder: Decoder[RequestError] = deriveDecoder[RequestError]
  implicit val otherErrorDecoder: Decoder[CompetitionEnteringError] =
    deriveDecoder[CompetitionEnteringError]

  implicit val errorDecoder: Decoder[ThreePlusError] = requestErrorDecoder
    .map(data => data: RequestError)
    .or(otherErrorDecoder.map(data => data: CompetitionEnteringError))

  implicit val errorEntityDecoder: EntityDecoder[IO, ThreePlusError] = jsonOf[IO, ThreePlusError]
}
