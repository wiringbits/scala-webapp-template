package net.wiringbits.common.models

import net.wiringbits.common.models.id.{UserId, UserTokenId}
import play.api.libs.json.{Format, Json}

import scala.util.Try

case class UserToken(userId: UserId, userTokenId: UserTokenId)

object UserToken {
  def validate(tokenStr: String): Option[UserToken] = {
    val splittedToken = tokenStr.split("_")

    splittedToken match
      case Array(userIdStr, userTokenIdStr) =>
        Try(UserToken(UserId.fromString(userIdStr), UserTokenId.fromString(userTokenIdStr))).toOption
      case _ => None
  }

  implicit val userTokenFormat: Format[UserToken] = Json.format[UserToken]
}
