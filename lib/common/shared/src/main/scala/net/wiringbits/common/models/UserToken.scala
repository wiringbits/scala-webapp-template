package net.wiringbits.common.models

import java.util.UUID
import scala.util.Try

case class UserToken(userId: UUID) {
  def validate(token: String): Option[UserToken] = {
    Try(UUID.fromString(token)).map(UserToken).toOption
  }
}
