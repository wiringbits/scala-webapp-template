package net.wiringbits.repositories.models

import java.time.Instant
import java.util.UUID

case class User(
    id: UUID,
    name: String,
    email: String,
    hashedPassword: String,
    createdAt: Instant
)

object User {
  case class CreateUser(id: UUID, name: String, email: String, hashedPassword: String)
}
