package net.wiringbits.api.models

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import net.wiringbits.common.models.Password
import play.api.libs.json.{Format, Json, OFormat}

object UpdatePassword {

  @ApiModel(value = "UpdatePasswordRequest", description = "Request to change the user's password")
  case class Request(
      @ApiModelProperty(value = "The user's old password", dataType = "String") oldPassword: Password,
      @ApiModelProperty(value = "The user's new password", dataType = "String") newPassword: Password
  )

  @ApiModel(value = "UpdatePasswordResponse", description = "Response after updating the user's password")
  case object Response
  type Response = Response.type
  implicit val updatePasswordRequestFormat: Format[Request] = Json.format[Request]
  implicit val updatePasswordResponseFormat: OFormat[Response] = RequestResponseCodec.requestResponseCodec(Response)
}
