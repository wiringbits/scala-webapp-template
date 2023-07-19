package net.wiringbits.api.models

import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import play.api.libs.json.{Format, Json}
import sttp.tapir.generic.auto.*
import sttp.tapir.Schema

import java.util.UUID

object Login {

  case class Request(email: Email, password: Password, captcha: Captcha)

  case class Response(id: UUID, name: Name, email: Email)

  implicit val loginRequestFormat: Format[Request] = Json.format[Request]
  implicit val loginResponseFormat: Format[Response] = Json.format[Response]

  implicit val loginRequestSchema: Schema[Request] = Schema.derived[Request].name(Schema.SName("LoginRequest"))
  implicit val loginResponseSchema: Schema[Response] = Schema.derived[Response].name(Schema.SName("LoginResponse"))
}
