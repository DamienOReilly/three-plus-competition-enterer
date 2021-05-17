package org.damienoreilly

import cats.data.EitherT
import cats.effect.IO
import org.http4s.Uri
import pureconfig.ConfigReader
import pureconfig.error.CannotConvert

package object tpce {
  type CompetitionResults = List[(Competition, EitherT[IO, ThreePlusError, CompetitionEntered])]
  type Result = EitherT[IO, ThreePlusError, CompetitionResults]

  implicit val uriReader: ConfigReader[Uri] =
    ConfigReader.fromString(str =>
      Uri
        .fromString(str)
        .fold(err => Left(CannotConvert(str, "Uri", err.sanitized)), uri => Right(uri))
    )

}
