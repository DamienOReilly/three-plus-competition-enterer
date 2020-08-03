package org.damienoreilly.tpce

import cats.effect.IO
import io.circe.Decoder
import io.circe.Decoder._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder, Uri}

sealed trait ThreePlusError extends RuntimeException

case class UnknownResponse(reason: String) extends ThreePlusError

case class RequestError(
  error: String,
  error_description: String
) extends ThreePlusError

case class FatalError(
  timestamp: Long,
  status: Int,
  error: String,
  exception: String,
  message: String,
  path: String
) extends ThreePlusError

case class CompetitionEnteringError(
  message: String,
  status: Int
) extends ThreePlusError

case class Token(
  access_token: String,
  token_type: String,
  expires_in: Long,
  scope: String,
  jti: String,
)

case class Competition(
  id: Int,
  category: Option[String],
  categorySecondary: Option[String],
  title: Option[String],
  subtitle: Option[String],
  `type`: Option[String],
  index: Option[Int],
  order: Option[Int],
  urlBannerImageLarge: Option[String],
  urlBannerImageLargeApp: Option[String],
  urlBannerImageMedium: Option[String],
  urlBannerImageMediumApp: Option[String],
  urlBannerImageSmall: Option[String],
  urlBannerImageSmallApp: Option[String],
  redirectionUrl: Option[String],
  distance: Option[String],
  help: Option[String],
  name: String,
  supplierName: Option[String],
  qrCode: Option[Boolean],
  urlName: Option[String],
  maxNbOfOffer: Option[Int],
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
  implicit val competitionEntityDecoder: EntityDecoder[IO, List[Competition]] = jsonOf[IO, List[Competition]]
  implicit val enteredDecoder: EntityDecoder[IO, CompetitionEntered] = jsonOf[IO, CompetitionEntered]
  implicit val enterEncoder: EntityEncoder[IO, EnterCompetition] = jsonEncoderOf[IO, EnterCompetition]

  implicit val requestErrorDecoder: Decoder[RequestError] = deriveDecoder[RequestError]
  implicit val fatalErrorDecoder: Decoder[FatalError] = deriveDecoder[FatalError]
  implicit val otherErrorDecoder: Decoder[CompetitionEnteringError] = deriveDecoder[CompetitionEnteringError]

  implicit val errorDecoder: Decoder[ThreePlusError] = requestErrorDecoder
    .map(data => data: RequestError)
    .or(
      fatalErrorDecoder
        .map(data => data: FatalError)
        .or(
          otherErrorDecoder.map(data => data: CompetitionEnteringError)
        )
    )
  implicit val errorEntityDecoder: EntityDecoder[IO, ThreePlusError] = jsonOf[IO, ThreePlusError]
}
