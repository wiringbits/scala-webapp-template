package net.wiringbits.api.models

import net.wiringbits.common.models.Password
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

object UpdatePassword {

  case class Request(oldPassword: Password, newPassword: Password)

  case class Response(noData: String = "")

  implicit val updatePasswordRequestFormat: Format[Request] = Json.format[Request]
  implicit val updatePasswordResponseFormat: Format[Response] = Json.format[Response]

  implicit val updatePasswordRequestSchema: Schema[Request] = Schema.derived
  implicit val updatePasswordResponseSchema: Schema[Response] = Schema.derived
}
