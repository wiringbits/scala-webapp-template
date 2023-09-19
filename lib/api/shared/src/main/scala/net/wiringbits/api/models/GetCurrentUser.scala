package net.wiringbits.api.models

import net.wiringbits.common.models.id.UserId
import net.wiringbits.common.models.{Email, InstantCustom, Name}
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema
import sttp.tapir.generic.auto.*

import java.time.Instant
import java.util.UUID

object GetCurrentUser {
  case class Response(userId: UserId, name: Name, email: Email, createdAt: InstantCustom)

  implicit val getUserResponseFormat: Format[Response] = Json.format[Response]

  implicit val getUserResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("GetCurrentUserResponse"))
    .description("Response to find the authenticated user details")
}
