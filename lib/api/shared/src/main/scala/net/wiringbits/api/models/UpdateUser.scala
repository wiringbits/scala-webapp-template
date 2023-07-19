package net.wiringbits.api.models

import net.wiringbits.common.models.Name
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

object UpdateUser {

  case class Request(name: Name)

  case class Response(noData: String = "")

  implicit val updateUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val updateUserResponseFormat: Format[Response] = Json.format[Response]

  implicit val updateUserRequestSchema: Schema[Request] = Schema.derived[Request]
  implicit val updateUserResponseSchema: Schema[Response] = Schema.derived[Response]
}
