package net.wiringbits.api.models

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import net.wiringbits.common.models.{Email, Name, Password, UserToken}
import play.api.libs.json.{Format, Json}

object ResetPassword {

  @ApiModel(value = "ResetPasswordRequest", description = "Request to reset a user password")
  case class Request(
      token: UserToken,
      @ApiModelProperty(value = "The user's password", dataType = "String") password: Password
  )

  @ApiModel(value = "ResetPasswordResponse", description = "Response after resetting a user password")
  case class Response(
      @ApiModelProperty(value = "The user's name", dataType = "String", example = "Alex")
      name: Name,
      @ApiModelProperty(value = "The user's email", dataType = "String")
      email: Email
  )

  implicit val userTokenFormat: Format[UserToken] = Json.format[UserToken]
  implicit val resetPasswordRequestFormat: Format[Request] = Json.format[Request]
  implicit val resetPasswordResponseFormat: Format[Response] = Json.format[Response]
}
