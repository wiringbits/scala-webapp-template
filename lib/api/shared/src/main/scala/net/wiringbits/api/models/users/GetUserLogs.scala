package net.wiringbits.api.models.users

import net.wiringbits.api.models.*
import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema
import sttp.tapir.generic.auto.*

import java.time.Instant
import java.util.UUID

object GetUserLogs {
  case class Response(data: List[Response.UserLog])

  object Response {
    case class UserLog(userLogId: UUID, message: String, createdAt: Instant)
    implicit val getUserLogsResponseFormat: Format[UserLog] = Json.format[UserLog]
    implicit val getUserLogsResponseSchema: Schema[UserLog] =
      Schema.derived[UserLog].name(Schema.SName("GetUserLogsResponseUserLog"))
  }

  implicit val getUserLogsResponseFormat: Format[Response] = Json.format[Response]

  implicit val getUserLogsResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("GetUserLogsResponse"))
    .description("Includes the authenticated user logs")
}
