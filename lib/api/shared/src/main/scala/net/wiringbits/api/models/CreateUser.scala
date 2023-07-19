package net.wiringbits.api.models

import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

import java.util.UUID

object CreateUser {
  case class Request(name: Name, email: Email, password: Password, captcha: Captcha)
  case class Response(id: UUID, name: Name, email: Email)

  implicit val createUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val createUserResponseFormat: Format[Response] = Json.format[Response]

  implicit val createUserResponseSchema: Schema[Response] =
    Schema.derived[Response].name(Schema.SName("CreateUserResponse"))
  implicit val createUserRequestSchema: Schema[Request] =
    Schema.derived[Request].name(Schema.SName("CreateUserRequest"))
}
