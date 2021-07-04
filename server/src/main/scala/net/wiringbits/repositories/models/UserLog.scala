package net.wiringbits.repositories.models

import java.time.Instant
import java.util.UUID

case class UserLog(userLogId: UUID, userId: UUID, message: String, createdAt: Instant)

object UserLog {
  case class CreateUserLog(userLogId: UUID, userId: UUID, message: String)
}
