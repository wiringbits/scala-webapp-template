package net.wiringbits.api.models.auth

import net.wiringbits.api.models.*
import net.wiringbits.common.models.{Email, Name}
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema

import java.time.Instant
import java.util.UUID

object GetCurrentUser {
  case class Response(id: UUID, name: Name, email: Email, createdAt: Instant)

  implicit val getUserResponseFormat: Format[Response] = Json.format[Response]

  implicit val getUserResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("GetCurrentUserResponse"))
    .description("Response to find the authenticated user details")
}
