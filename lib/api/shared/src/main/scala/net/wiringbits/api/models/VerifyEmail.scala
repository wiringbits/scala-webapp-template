package net.wiringbits.api.models

import net.wiringbits.common.models.UserToken
import play.api.libs.json.{Format, Json}

object VerifyEmail {
  case class Request(
      token: UserToken
  )
  case class Response(
      noData: String = ""
  )

  implicit val userTokenFormat: Format[UserToken] = Json.format[UserToken]
  implicit val verifyEmailRequestFormat: Format[Request] = Json.format[Request]
  implicit val verifyEmailResponseFormat: Format[Response] = Json.format[Response]
}
