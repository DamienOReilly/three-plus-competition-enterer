package org.damienoreilly

import cats.data.EitherT
import cats.effect.IO

package object tpce {
  type Result = EitherT[IO, ThreePlusError, CompetitionResults]
  type CompetitionResults = List[(Competition, EitherT[IO, ThreePlusError, CompetitionEntered])]
}
