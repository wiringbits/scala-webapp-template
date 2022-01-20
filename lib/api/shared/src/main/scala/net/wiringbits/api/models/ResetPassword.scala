package net.wiringbits.api.models

import net.wiringbits.common.models.{Password, UserToken}
import play.api.libs.json.{Format, Json}

object ResetPassword {
  case class Request(token: UserToken, password: Password)
  case class Response(token: Option[String])

  implicit val userTokenFormat: Format[UserToken] = Json.format[UserToken]
  implicit val resetPasswordRequestFormat: Format[Request] = Json.format[Request]
  implicit val resetPasswordResponseFormat: Format[Response] = Json.format[Response]
}
