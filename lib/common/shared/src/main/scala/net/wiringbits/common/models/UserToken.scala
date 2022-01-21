package net.wiringbits.common.models

import java.util.UUID
import scala.util.Try

case class UserToken(userId: UUID, token: UUID)

object UserToken {
  // TODO: Add tests to this method
  def validate(tokenStr: String): Option[UserToken] = {
    val splittedToken = tokenStr.split("_")
    val isValid = splittedToken.length == 2

    // TODO: Improve this impl
    Try(
      Option.when(isValid)(UserToken(UUID.fromString(splittedToken(0)), UUID.fromString(splittedToken(1))))
    ).toOption.flatten
  }
}
