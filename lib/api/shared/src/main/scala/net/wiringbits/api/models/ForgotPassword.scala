package net.wiringbits.api.models

import net.wiringbits.common.models.{Captcha, Email}
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

object ForgotPassword {
  case class Request(email: Email, captcha: Captcha)

  case class Response(noData: String = "")

  implicit val forgotPasswordRequestFormat: Format[Request] = Json.format[Request]
  implicit val forgotPasswordResponseFormat: Format[Response] = Json.format[Response]

  implicit val forgotPasswordResponseSchema: Schema[Response] = Schema.derived
  implicit val forgotPasswordRequestSchema: Schema[Request] = Schema.derived
}
