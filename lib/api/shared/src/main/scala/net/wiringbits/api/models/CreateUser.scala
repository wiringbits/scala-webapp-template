package net.wiringbits.api.models

import io.swagger.annotations._
import net.wiringbits.common.models.{Captcha, Email, Name, Password}
import play.api.libs.json.{Format, Json}

import java.util.UUID

object CreateUser {
  @ApiModel(value = "CreateUserRequest", description = "Request for the create user API")
  case class Request(
      @ApiModelProperty(value = "The user's name", dataType = "String", example = "Alex")
      name: Name,
      @ApiModelProperty(value = "The user's email", dataType = "String", example = "email@wiringbits.net")
      email: Email,
      @ApiModelProperty(value = "The user's password", dataType = "String", example = "notSoWeakPassword")
      password: Password,
      @ApiModelProperty(value = "The ReCAPTCHA value", dataType = "String")
      captcha: Captcha
  )
  @ApiModel(value = "CreateUserResponse", description = "Response for the create user API")
  case class Response(
      @ApiModelProperty(
        value = "The id for the created user",
        dataType = "String",
        example = "e9e8d358-b989-4dd1-834d-764cac539fb1"
      )
      id: UUID,
      @ApiModelProperty(value = "The name for the created user", dataType = "String", example = "email@wiringbits.net")
      name: Name,
      @ApiModelProperty(value = "The email for the created user", dataType = "String", example = "Alex")
      email: Email
  )

  implicit val createUserRequestFormat: Format[Request] = Json.format[Request]
  implicit val createUserResponseFormat: Format[Response] = Json.format[Response]
}
