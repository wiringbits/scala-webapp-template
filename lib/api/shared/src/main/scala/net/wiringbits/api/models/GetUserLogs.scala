package net.wiringbits.api.models

import io.swagger.annotations.ApiModel
import play.api.libs.json.{Format, Json,OFormat,OWrites,JsError,JsSuccess,Reads,JsObject}

import java.time.Instant
import java.util.UUID

object GetUserLogs {
  case class  Request()

  @ApiModel(value = "GetUserLogsResponse", description = "Includes the authenticated user logs")
  case class Response(data: List[Response.UserLog])

  object Response {
    case class UserLog(id: UUID, message: String, createdAt: Instant)
    implicit val getUserLogsResponseFormat: Format[UserLog] = Json.format[UserLog]
  }

  //implicit val getUserLogsRequestFormat: Format[Request] = Json.format[Request]
  implicit val getUserLogsRequestFormat = OFormat[Request](Reads[Request] {
         case JsObject(_) => JsSuccess(Request())
         case _           => JsError("Empty object expected")
       }, OWrites[Request] { _ =>
         Json.obj()
       })
  
  implicit val getUserLogsResponseFormat: Format[Response] = Json.format[Response]
}
