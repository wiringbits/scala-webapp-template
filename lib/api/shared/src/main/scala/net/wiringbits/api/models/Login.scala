package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}

object Login {
  case class Request(email: String, password: String)
  case class Response(name: String, email: String, token: String)
  implicit val loginRequestFormat: Format[Request] = Json.format[Request]
  implicit val loginResponseFormat: Format[Response] = Json.format[Response]
}
