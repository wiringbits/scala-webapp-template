package net.wiringbits.api.models

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import net.wiringbits.common.models.Password
import play.api.libs.json.{Format, Json,OFormat,OWrites,JsError,JsSuccess,Reads,JsObject}

object UpdatePassword {

  @ApiModel(value = "UpdatePasswordRequest", description = "Request to change the user's password")
  case class Request(
      @ApiModelProperty(value = "The user's old password", dataType = "String") oldPassword: Password,
      @ApiModelProperty(value = "The user's new password", dataType = "String") newPassword: Password
  )

  @ApiModel(value = "UpdatePasswordResponse", description = "Response after updating the user's password")
  case class  Response()

  implicit val updatePasswordRequestFormat: Format[Request] = Json.format[Request]
  //implicit val updatePasswordResponseFormat: Format[Response] = Json.format[Response]

  implicit val updatePasswordResponseFormat = OFormat[Response](Reads[Response] {
        case JsObject(_) => JsSuccess(Response())
        case _           => JsError("Empty object expected")
      }, OWrites[Response] { _ =>
        Json.obj()
      })
}
