package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}

import java.time.Instant
import java.util.UUID

object AdminGetUsers {
  case class Response(data: List[Response.User])
  implicit val adminGetUsersResponseFormat: Format[Response] = Json.format[Response]

  object Response {
    case class User(id: UUID, name: String, email: String, createdAt: Instant)
    implicit val adminGetUsersResponseUserFormat: Format[User] = Json.format[User]
  }
}
