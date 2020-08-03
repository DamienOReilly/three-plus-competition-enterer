package org.damienoreilly

import cats.data.EitherT
import cats.effect.IO

package object tpce {
  type CompetitionResults = List[(Competition, EitherT[IO, ThreePlusError, CompetitionEntered])]
  type Result = EitherT[IO, ThreePlusError, CompetitionResults]
}
