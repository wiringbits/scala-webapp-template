package net.wiringbits.api.models.auth

import net.wiringbits.api.models.*
import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema
import sttp.tapir.generic.auto.*

import java.util.UUID

object Login {

  case class Request(email: Email, password: Password, captcha: Captcha)

  case class Response(id: UUID, name: Name, email: Email)

  implicit val loginRequestFormat: Format[Request] = Json.format[Request]
  implicit val loginResponseFormat: Format[Response] = Json.format[Response]

  implicit val loginRequestSchema: Schema[Request] = Schema
    .derived[Request]
    .name(Schema.SName("LoginRequest"))
    .description("Request to log into the app")
  implicit val loginResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("LoginResponse"))
    .description("Response after logging into the app")
}
