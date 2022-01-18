package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}

object UpdateUser {
  case class Request(name: String)
  case class Response(noData: String = "")
  implicit val updateUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val updateUserResponseFormat: Format[Response] = Json.format[Response]
}
