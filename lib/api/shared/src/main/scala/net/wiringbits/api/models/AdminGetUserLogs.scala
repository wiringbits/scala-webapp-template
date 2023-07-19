package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema
import sttp.tapir.generic.auto.*

import java.time.Instant
import java.util.UUID

object AdminGetUserLogs {
  case class Response(data: List[Response.UserLog])
  implicit val adminGetUserLogsResponseFormat: Format[Response] = Json.format[Response]
  implicit val adminGetUserLogsResponseSchema: Schema[Response] =
    Schema.derived[Response].name(Schema.SName("AdminGetUserLogsResponse"))

  object Response {
    case class UserLog(id: UUID, message: String, createdAt: Instant)
    implicit val adminGetUserLogsResponseUserLogFormat: Format[UserLog] = Json.format[UserLog]
  }
}
