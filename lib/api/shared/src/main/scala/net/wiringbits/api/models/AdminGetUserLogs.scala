package net.wiringbits.api.models

import io.swagger.annotations.ApiModel
import play.api.libs.json.{Format, Json}

import java.time.Instant
import java.util.UUID

object AdminGetUserLogs {
  @ApiModel(value = "AdminGetUserLogsResponse", description = "Includes the logs for a single user")
  case class Response(data: List[Response.UserLog])
  implicit val adminGetUserLogsResponseFormat: Format[Response] = Json.format[Response]

  object Response {
    case class UserLog(id: UUID, message: String, createdAt: Instant)
    implicit val adminGetUserLogsResponseUserLogFormat: Format[UserLog] = Json.format[UserLog]
  }
}
