package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema
import sttp.tapir.generic.auto.*

import java.time.Instant
import java.util.UUID

object AdminGetUserLogs {
  case class Response(data: List[Response.UserLog])
  implicit val adminGetUserLogsResponseFormat: Format[Response] = Json.format[Response]
  implicit val adminGetUserLogsResponseSchema: Schema[Response] = Schema
    .derived[Response]
    .name(Schema.SName("AdminGetUserLogsResponse"))
    .description("Includes the logs for a single user")

  object Response {
    case class UserLog(userLogId: UUID, message: String, createdAt: Instant)
    implicit val adminGetUserLogsResponseUserLogFormat: Format[UserLog] = Json.format[UserLog]
    implicit val adminGetUserLogsResponseUserLogSchema: Schema[UserLog] = Schema
      .derived[UserLog]
      .name(Schema.SName("AdminGetUserLogsResponseUserLog"))
  }
}
