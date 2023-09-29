package net.wiringbits.api.models.users

import net.wiringbits.api.models.*
import net.wiringbits.common.models.{Captcha, Email}
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

object ForgotPassword {
  case class Request(email: Email, captcha: Captcha)

  case class Response(noData: String = "")

  implicit val forgotPasswordRequestFormat: Format[Request] = Json.format[Request]
  implicit val forgotPasswordResponseFormat: Format[Response] = Json.format[Response]

  implicit val forgotPasswordRequestSchema: Schema[Request] = Schema
    .derived[Request]
    .name(Schema.SName("ForgotPasswordRequest"))
    .description("Request to reset a forgotten password")
  implicit val forgotPasswordResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("ForgotPasswordResponse"))
    .description("Response to the ForgotPasswordRequest")
}
