package net.wiringbits.api.models

import play.api.libs.json.{Format, Json}
import sttp.tapir.Schema
import sttp.tapir.generic.auto.*

import java.time.Instant
import java.util.UUID

object GetUserLogs {
  case class Request(noData: String = "")

  case class Response(data: List[Response.UserLog])

  object Response {
    case class UserLog(id: UUID, message: String, createdAt: Instant)
    implicit val getUserLogsResponseFormat: Format[UserLog] = Json.format[UserLog]
  }

  implicit val getUserLogsRequestFormat: Format[Request] = Json.format[Request]
  implicit val getUserLogsResponseFormat: Format[Response] = Json.format[Response]

  implicit val getUserLogsRequestSchema: Schema[Request] = Schema.derived
  implicit val getUserLogsResponseSchema: Schema[Response] = Schema.derived
}
