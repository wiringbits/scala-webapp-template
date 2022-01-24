package net.wiringbits.util

import net.wiringbits.repositories.models.{UserToken, UserTokenType}

import java.time.{Clock, Instant}
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.duration.FiniteDuration

class TokenGenerator @Inject() () {
  def create(userId: UUID, token: String, tokenType: UserTokenType, expirationTime: FiniteDuration)(implicit
      clock: Clock
  ): UserToken.Create = {
    UserToken.Create(
      id = UUID.randomUUID(),
      token = token,
      tokenType = tokenType,
      createdAt = Instant.now(clock),
      expiresAt = Instant.now(clock).plus(expirationTime.toHours, ChronoUnit.HOURS),
      userId = userId
    )
  }
}
