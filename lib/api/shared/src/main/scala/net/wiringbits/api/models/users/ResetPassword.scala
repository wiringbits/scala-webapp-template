package net.wiringbits.api.models.users

import net.wiringbits.api.models.*
import net.wiringbits.common.models.{Email, Name, Password, UserToken}
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema
import sttp.tapir.generic.auto.*

object ResetPassword {

  case class Request(token: UserToken, password: Password)

  case class Response(name: Name, email: Email)

  implicit val resetPasswordRequestFormat: Format[Request] = Json.format[Request]
  implicit val resetPasswordResponseFormat: Format[Response] = Json.format[Response]

  implicit val resetPasswordRequestSchema: Schema[Request] = Schema
    .derived[Request]
    .name(Schema.SName("ResetPasswordRequest"))
    .description("Request to reset a user password")
  implicit val resetPasswordResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("ResetPasswordResponse"))
    .description("Response after resetting a user password")
}
