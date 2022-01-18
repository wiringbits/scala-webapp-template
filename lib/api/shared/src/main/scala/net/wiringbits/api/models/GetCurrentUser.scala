package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}

import java.util.UUID

object GetCurrentUser {
  case class Request(noData: String = "")
  case class Response(id: UUID, name: String, email: String)
  implicit val getUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val getUserResponseFormat: Format[Response] = Json.format[Response]
}
