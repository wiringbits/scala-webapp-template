package net.wiringbits.api.models

import io.swagger.annotations.ApiModel
import play.api.libs.json.{Format, Json, OFormat}

import java.time.Instant
import java.util.UUID

object GetUserLogs {
  case object Request
  type Request = Request.type
  @ApiModel(value = "GetUserLogsResponse", description = "Includes the authenticated user logs")
  case class Response(data: List[Response.UserLog])

  object Response {
    case class UserLog(id: UUID, message: String, createdAt: Instant)
    implicit val getUserLogsResponseFormat: Format[UserLog] = Json.format[UserLog]
  }

  implicit val getUserLogsRequestFormat: OFormat[Request] = RequestResponseCodec.requestResponseCodec(Request)

  implicit val getUserLogsResponseFormat: Format[Response] = Json.format[Response]
}
