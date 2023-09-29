package net.wiringbits.api.models.users

import net.wiringbits.api.models.*
import net.wiringbits.common.models.Name
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

object UpdateUser {

  case class Request(name: Name)

  case class Response(noData: String = "")

  implicit val updateUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val updateUserResponseFormat: Format[Response] = Json.format[Response]

  implicit val updateUserRequestSchema: Schema[Request] = Schema
    .derived[Request]
    .name(Schema.SName("UpdateUserRequest"))
    .description("Request to update user details")
  implicit val updateUserResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("UpdateUserResponse"))
    .description("Response after updating the user details")
}
