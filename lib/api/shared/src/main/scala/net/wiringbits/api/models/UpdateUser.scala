package net.wiringbits.api.models

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import net.wiringbits.common.models.Name
import play.api.libs.json.{Format, Json}

object UpdateUser {

  @ApiModel(value = "UpdateUserRequest", description = "Request to update user details")
  case class Request(@ApiModelProperty(value = "The user's name", dataType = "String", example = "Alex") name: Name)

  @ApiModel(value = "UpdateUserResponse", description = "Response after updating the user details")
  case class Response(noData: String = "")

  implicit val updateUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val updateUserResponseFormat: Format[Response] = Json.format[Response]
}
