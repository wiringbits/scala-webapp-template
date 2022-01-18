package net.wiringbits.repositories.models

import net.wiringbits.apis.models.TokenType

import java.time.{Clock, Instant}
import java.time.temporal.ChronoUnit
import java.util.UUID
import scala.concurrent.duration.FiniteDuration

case class Token(
    id: UUID,
    token: UUID,
    tokenType: TokenType,
    createdAt: Instant,
    expiresAt: Instant,
    userId: UUID
)

object Token {
  case class CreateToken(
      id: UUID,
      token: UUID,
      tokenType: TokenType,
      userId: UUID
  ) {
    def expirationHour(duration: FiniteDuration)(implicit clock: Clock): Instant = {
      Instant.now(clock).plus(duration.toHours, ChronoUnit.HOURS)
    }
  }
}
