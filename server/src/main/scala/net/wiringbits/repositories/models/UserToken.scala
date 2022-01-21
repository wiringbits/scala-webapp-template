package net.wiringbits.repositories.models

import java.time.Instant
import java.util.UUID

case class UserToken(
    id: UUID,
    token: UUID,
    tokenType: UserTokenType,
    createdAt: Instant,
    expiresAt: Instant,
    userId: UUID
)

object UserToken {

  case class Create(
      id: UUID,
      token: UUID,
      tokenType: UserTokenType,
      createdAt: Instant,
      expiresAt: Instant,
      userId: UUID
  )
}
