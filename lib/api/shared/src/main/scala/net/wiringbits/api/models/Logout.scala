package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

object Logout {

  case class Request(noData: String = "")

  case class Response(noData: String = "")

  implicit val logoutRequestFormat: Format[Request] = Json.format[Request]
  implicit val logoutResponseFormat: Format[Response] = Json.format[Response]

  implicit val logoutRequestSchema: Schema[Request] = Schema.derived[Request]
  implicit val logoutResponseSchema: Schema[Response] = Schema.derived[Response]
}
