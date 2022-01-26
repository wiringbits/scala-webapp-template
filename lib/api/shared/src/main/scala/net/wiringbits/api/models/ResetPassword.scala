package net.wiringbits.api.models

import net.wiringbits.common.models.{Email, Name, Password, UserToken}
import play.api.libs.json.{Format, Json}

object ResetPassword {
  case class Request(token: UserToken, password: Password)
  case class Response(name: Name, email: Email, token: String)

  implicit val userTokenFormat: Format[UserToken] = Json.format[UserToken]
  implicit val resetPasswordRequestFormat: Format[Request] = Json.format[Request]
  implicit val resetPasswordResponseFormat: Format[Response] = Json.format[Response]
}
