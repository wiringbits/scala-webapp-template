package net.wiringbits.common.models

import java.util.UUID
import scala.util.Try

case class UserToken(userId: UUID)

object UserToken {
  def validate(tokenStr: String): Option[UserToken] = {
    Try(UUID.fromString(tokenStr)).map(new UserToken(_)).toOption
  }
}
