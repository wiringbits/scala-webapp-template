package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}

import java.util.UUID

object CreateUser {
  case class Request(name: String, email: String, password: String)
  case class Response(id: UUID, name: String, email: String)
  implicit val createUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val createUserResponseFormat: Format[Response] = Json.format[Response]
}
