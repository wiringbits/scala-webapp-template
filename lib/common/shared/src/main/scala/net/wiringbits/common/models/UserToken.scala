package net.wiringbits.common.models

import play.api.libs.json.{Format, Json}

import java.util.UUID
import scala.util.Try

case class UserToken(userId: UUID, token: UUID)

object UserToken {
  def validate(tokenStr: String): Option[UserToken] = {
    val splittedToken = tokenStr.split("_")
    val isValid = splittedToken.length == 2

    // TODO: Improve this impl
    Try(
      Option.when(isValid)(UserToken(UUID.fromString(splittedToken(0)), UUID.fromString(splittedToken(1))))
    ).toOption.flatten
  }

  implicit val userTokenFormat: Format[UserToken] = Json.format[UserToken]
}
