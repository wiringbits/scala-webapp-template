package net.wiringbits.models

import java.util.UUID

case class UserToken(userId: UUID)

object UserToken {
  def validate(userToken: String): Option[UserToken] = {
    val userId = UUID.fromString(userToken)
    Some(UserToken(userId))
  }
}
