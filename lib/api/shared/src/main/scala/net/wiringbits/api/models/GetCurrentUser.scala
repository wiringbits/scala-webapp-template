package net.wiringbits.api.models

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import net.wiringbits.common.models.{Email, Name}
import play.api.libs.json.{Format, Json,OFormat,OWrites,JsError,JsSuccess,Reads,JsObject}

import java.time.Instant
import java.util.UUID

object GetCurrentUser {
  case class Request()

  @ApiModel(value = "GetCurrentUserRequest", description = "Request to find the authenticated user details")
  case class Response(
      @ApiModelProperty(
        value = "The id for the user",
        dataType = "String",
        example = "e9e8d358-b989-4dd1-834d-764cac539fb1"
      )
      id: UUID,
      @ApiModelProperty(value = "The name for the user", dataType = "String", example = "email@wiringbits.net")
      name: Name,
      @ApiModelProperty(value = "The email for the user", dataType = "String", example = "Alex")
      email: Email,
      @ApiModelProperty(
        value = "The timestamp when the user was created",
        dataType = "String",
        example = "2022-03-30T18:18:25.575123Z"
      )
      createdAt: Instant
  )
  
  //implicit val getUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val getUserRequestFormat = OFormat[Request](Reads[Request] {
           case JsObject(_) => JsSuccess(Request())
           case _           => JsError("Empty object expected")
         }, OWrites[Request] { _ =>
           Json.obj()
         })
  implicit val getUserResponseFormat: Format[Response] = Json.format[Response]
}
