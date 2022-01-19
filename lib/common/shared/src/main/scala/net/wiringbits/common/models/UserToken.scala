package net.wiringbits.common.models

import java.util.UUID
import scala.util.Try

class UserToken private (val userId: UUID)

object UserToken {

  def validate(tokenStr: String): Option[UserToken] = {
    Try(UUID.fromString(tokenStr)).map(x => new UserToken(x)).toOption
  }

  def trusted(token: UUID): UserToken = new UserToken(token)
}
