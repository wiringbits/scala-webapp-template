package net.wiringbits.models

import java.util.UUID

case class UserToken(userId: UUID)

object UserToken {
  def validate(userToken: String): Option[UserToken] = {
    try {
      Some(UserToken(UUID.fromString(userToken)))
    } catch {
      case _: Exception => None
    }
  }
}
