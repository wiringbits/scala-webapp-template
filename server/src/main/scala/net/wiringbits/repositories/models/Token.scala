package net.wiringbits.repositories.models

import java.time.{Clock, Instant}
import java.util.UUID
import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}

import java.time.temporal.ChronoUnit
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

sealed trait TokenType extends EnumEntry with Uppercase

object TokenType extends Enum[TokenType] {
  final case object VerificationToken extends TokenType
  final case object ResetPasswordToken extends TokenType

  val values = findValues
}
