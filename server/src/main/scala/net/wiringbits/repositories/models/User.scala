package net.wiringbits.repositories.models

import net.wiringbits.common.models.{Email, Name}

import java.time.Instant
import java.util.UUID

case class User(
    id: UUID,
    name: Name,
    email: Email,
    hashedPassword: String,
    createdAt: Instant,
    verifiedOn: Option[Instant]
)

object User {
  case class CreateUser(id: UUID, name: Name, email: Email, hashedPassword: String, verifyEmailToken: String)
}
