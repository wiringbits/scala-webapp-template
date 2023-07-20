package net.wiringbits.api.models

import net.wiringbits.common.models.{Email, Name}
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema
import sttp.tapir.generic.auto.*

import java.time.Instant
import java.util.UUID

object AdminGetUsers {

  case class Response(data: List[Response.User])
  implicit val adminGetUsersResponseFormat: Format[Response] = Json.format[Response]
  implicit val adminGetUsersResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("AdminGetUsersResponse"))
    .description("Includes the user list")

  object Response {
    case class User(id: UUID, name: Name, email: Email, createdAt: Instant)
    implicit val adminGetUsersResponseUserFormat: Format[User] = Json.format[User]
    implicit val adminGetUsersResponseUserSchema: Schema[User] = Schema
      .derived[User]
      .name(Schema.SName("AdminGetUsersResponseUser"))
  }
}
