package net.wiringbits.models

import java.util.UUID

case class UserToken(userId: UUID, token: UUID)

object UserToken {
  def validate(userToken: String): Option[UserToken] = {
    val splittedToken = userToken.split("_")
    val isValid = splittedToken.length == 2

    if (isValid) {
      val userId = splittedToken(0)
      val token = splittedToken(1)
      Some(UserToken(userId = UUID.fromString(userId), token = UUID.fromString(token)))
    } else None
  }
}
